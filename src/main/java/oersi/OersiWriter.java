package oersi;

import static java.util.stream.Collectors.joining;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.metafacture.framework.ObjectReceiver;

/**
 * Writes strings to the OERSI backend API
 *
 * @author Fabian Steeg (fsteeg)
 *
 */

public final class OersiWriter implements ObjectReceiver<String> {

    private static final Logger LOG = Logger.getLogger(OersiWriter.class.getName());
    private static final String TYPE = "application/json";

    private String url;
    private String user;
    private String pass;

    private HttpClient client;
    private List<HttpResponse<String>> responses = new ArrayList<>();

    public OersiWriter(final String url) {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass.toCharArray());
            }
        };
        client = HttpClient.newBuilder().authenticator(auth).build();
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public void process(final String obj) {
        HttpRequest request = HttpRequest.newBuilder()//
                .uri(URI.create(url))//
                .setHeader("content-type", TYPE)//
                .POST(HttpRequest.BodyPublishers.ofString(obj)).build();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            responses.add(response);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void resetStream() {
        responses = new ArrayList<>();
    }

    @Override
    public void closeStream() {
        try (FileWriter r = new FileWriter("data/to-oersibackend/responses.json")) {
            r.write("[\n");
            r.write(responses.stream().map(HttpResponse::body).collect(joining(",\n")));
            r.write("\n]");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        long success = responses.stream().filter(r -> r.statusCode() == 200).count();
        long fail = responses.stream().filter(r -> r.statusCode() != 200).count();
        LOG.log(Level.INFO, "Success: {0}, Fail: {1}", new Object[] { success, fail });
    }

}
