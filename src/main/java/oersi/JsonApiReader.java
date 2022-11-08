package oersi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads from a JSON web API and emits records as JSON strings.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class JsonApiReader extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonApiReader.class);
    private static final String JSON = "application/json";
    private int totalLimit = Integer.MAX_VALUE;
    private int totalProcessed = 0;
    private int wait = 1000;

    private String recordPath;
    private String body;
    private String pageParam;
    private String method;
    private int stepSize;
    private final Map<String, String> headers = new HashMap<>();

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPageParam(String pageParam) {
        this.pageParam = pageParam;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public void setTotalLimit(final int totalLimit) {
        this.totalLimit = totalLimit < 0 ? Integer.MAX_VALUE : totalLimit;
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeader(final String header) {
        Arrays.stream(Pattern.compile("\n").split(header)).forEach(h -> {
            final String[] parts = Pattern.compile(":").split(h, 2);
            if (parts.length == 2) {
                headers.put(parts[0].toLowerCase(), parts[1].trim());
            } else {
                throw new IllegalArgumentException("Invalid header: " + h);
            }
        });
    }

    @Override
    public void process(final String url) {
        LOG.debug("Processing JSON API from URL {}", url);
        try {
            HttpURLConnection connection = openUrlConnection(url);
            JSONObject jsonObject = new JSONObject(new JSONTokener(connection.getInputStream()));
            JSONArray jsonArray = jsonObject.getJSONArray(recordPath);
            for (int i = 0; i < Math.min(totalLimit, jsonArray.length())
                    && totalProcessed < totalLimit; i++, totalProcessed++) {
                String jsonRecord = jsonArray.get(i).toString();
                LOG.trace("Processing record {}", jsonRecord);
                getReceiver().process(jsonRecord);
            }
            if (totalProcessed < totalLimit && jsonArray.length() > 0) {
                Thread.sleep(wait);
                tryNextPage(url, stepSize);
            }
        } catch (IOException e) {
            throw new MetafactureException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MetafactureException(e);
        }
    }

    private HttpURLConnection openUrlConnection(final String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method.toUpperCase());
        connection.addRequestProperty("accept", JSON);
        connection.addRequestProperty("content-type", JSON);
        headers.forEach(connection::addRequestProperty);
        if (body != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        return connection;
    }

    private void tryNextPage(final String url, int currentPageSize) {
        boolean pagingIsSupported = url.contains(pageParam);
        boolean isDone = currentPageSize == 0 || totalLimit <= currentPageSize;
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
