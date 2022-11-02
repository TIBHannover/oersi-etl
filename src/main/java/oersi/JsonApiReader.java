package oersi;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.metafacture.framework.helpers.DefaultObjectReceiver;
import org.metafacture.io.HttpOpener;
import org.metafacture.io.HttpOpener.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

/**
 * Reads from a JSON web API and emits records as JSON strings.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class JsonApiReader extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonApiReader.class);
    private static final String JSON = "application/json";
    private int limit = Integer.MAX_VALUE;
    private int totalProcessed = 0;
    private int wait = 1000;

    private String recordPath;
    private String body;
    private String pageParam;
    private Method method;
    private int stepSize;
    private String header;

    public void setHeader(String header) {
        this.header = header;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setPageParam(String pageParam) {
        this.pageParam = pageParam;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public void setLimit(final int limit) {
        this.limit = limit < 0 ? Integer.MAX_VALUE : limit;
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void process(final String url) {
        LOG.debug("Processing JSON API from URL {}", url);
        HttpOpener opener = httpOpener(url);
        opener.process(body);
    }

    private HttpOpener httpOpener(final String url) {
        HttpOpener opener = new HttpOpener();
        opener.setAccept(JSON);
        opener.setContentType(JSON);
        opener.setUrl(url);
        opener.setMethod(method);
        if (header != null) {
            opener.setHeader(header);
        }
        opener.setReceiver(responseReceiver(url));
        return opener;
    }

    private ObjectReceiver<Reader> responseReceiver(final String url) {
        return new DefaultObjectReceiver<Reader>() {
            @Override
            public void process(Reader obj) {
                try {
                    String jsonString = CharStreams.toString(obj);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray(recordPath);
                    for (int i = 0; i < Math.min(limit, jsonArray.length())
                            && totalProcessed < limit; i++, totalProcessed++) {
                        String jsonRecord = jsonArray.get(i).toString();
                        LOG.trace("Processing record {}", jsonRecord);
                        getReceiver().process(jsonRecord);
                    }
                    if (totalProcessed < limit && jsonArray.length() > 0) {
                        Thread.sleep(wait);
                        tryNextPage(url, stepSize);
                    }
                } catch (JSONException | IOException e) {
                    throw new MetafactureException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new MetafactureException(e);
                }
            }
        };
    }

    private void tryNextPage(final String url, int currentPageSize) {
        boolean pagingIsSupported = url.contains(pageParam);
        boolean isDone = currentPageSize == 0 || limit <= currentPageSize;
        if (pagingIsSupported && !isDone) {
            String substring = url.substring(url.indexOf(pageParam) + pageParam.length())
                    .split("&")[0];
            try (Scanner scanner = new Scanner(substring)) {
                if (scanner.hasNextInt()) {
                    int lastFrom = scanner.nextInt();
                    int nextFrom = lastFrom + currentPageSize;
                    String replace = url.replace(pageParam + lastFrom, pageParam + nextFrom);
                    process(replace);
                }
            }
        }
    }

}
