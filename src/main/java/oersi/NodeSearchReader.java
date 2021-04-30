package oersi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads edu-sharing resources via node search REST API and emits URLs.
 * 
 * See https://www.oerbw.de/edu-sharing/swagger/#!/NODE_v1/getNodes
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class NodeSearchReader extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(NodeSearchReader.class);

    private int limit = Integer.MAX_VALUE;
    private int wait = 1000;

    private String findAndReplace;

    private HttpClient client = HttpClient.newBuilder().build();
    private final ObjectMapper mapper = new ObjectMapper();

    public void setLimit(final int limit) {
        this.limit = limit < 0 ? Integer.MAX_VALUE : limit;
    }

    public void setFindAndReplace(final String findAndReplace) {
        this.findAndReplace = findAndReplace;
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @Override
    public void process(String nodeApi) {
        LOG.debug("Processing nodeApi URL {}", nodeApi);
        try {
            HttpRequest request = HttpRequest.newBuilder()//
                    .uri(URI.create(nodeApi))//
                    .setHeader("Accept", "application/json")//
                    .POST(HttpRequest.BodyPublishers.ofString("")).build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode jsonResponse = mapper.readTree(response.body());
                emitResourceUrls(jsonResponse);
                int size = jsonResponse.get("nodes").size();
                String nextUrl = SitemapReader.getNextUrl(nodeApi, "skipCount=", size, limit);
                if (nextUrl != null) {
                    process(nextUrl);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MetafactureException(e.getMessage(), e);
        } catch (Exception e) {
            throw new MetafactureException(e.getMessage(), e);
        }
    }

    private void emitResourceUrls(JsonNode node) {
        node.get("nodes").iterator().forEachRemaining(n -> {
            String url = SitemapReader.findAndReplace(findAndReplace,
                    n.get("ref").get("id").asText());
            getReceiver().process(url);
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MetafactureException(e.getMessage(), e);
            }
        });
    }
}
