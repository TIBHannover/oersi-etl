package oersi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

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
    private long wait = Duration.ofSeconds(1).toMillis();
    private long timeout = Duration.ofMinutes(3).toMillis();

    private String recordPath;
    private String body;
    private String pageParam;
    private boolean pageInBody;
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

    public void setPageInBody(boolean pageInBody) {
        this.pageInBody = pageInBody;
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

    public void setTimeout(int millis) {
        this.timeout = millis;
    }

    @Override
    public void process(final String url) {
        LOG.debug("Processing JSON API from URL {}", url);
        try {
            HttpURLConnection connection = openUrlConnection(url);
            String jsonString = new String(connection.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
            List<Map<String, Object>> jsonArray = JsonPath.read(jsonString, recordPath);
            for (int i = 0; i < Math.min(totalLimit, jsonArray.size())
                    && totalProcessed < totalLimit; i++, totalProcessed++) {
                Map<String, Object> jsonRecord = jsonArray.get(i);
                LOG.trace("Processing record {}", jsonRecord);
                getReceiver().process(new JSONObject(jsonRecord).toString());
            }
            if (totalProcessed < totalLimit && !jsonArray.isEmpty()) {
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
        connection.setConnectTimeout((int) timeout);
        connection.setReadTimeout((int) timeout);
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
        if (currentPageSize == 0 || totalLimit <= currentPageSize) {
            return;
        }
        if (pageInBody) {
            JSONObject jsonBody = new JSONObject(body);
            int lastFrom = jsonBody.getInt(pageParam);
            int nextFrom = lastFrom + currentPageSize;
            jsonBody.put(pageParam, nextFrom);
            body = jsonBody.toString();
            process(url);
        } else {
            try (Scanner scanner = new Scanner(
                    url.substring(url.indexOf(pageParam) + pageParam.length() + 1).split("&")[0])) {
                if (scanner.hasNextInt()) {
                    int lastFrom = scanner.nextInt();
                    int nextFrom = lastFrom + currentPageSize;
                    String param = pageParam + "=";
                    process(url.replace(param + lastFrom, param + nextFrom));
                }
            }
        }
    }

}
