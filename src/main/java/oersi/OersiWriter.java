package oersi;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.metafacture.framework.ObjectReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes strings to the OERSI backend API
 *
 * @author Fabian Steeg (fsteeg)
 *
 */

public final class OersiWriter implements ObjectReceiver<String> {

    private static final Logger LOG = LoggerFactory.getLogger(OersiWriter.class);
    private static final String TYPE = "application/json";

    private String url;
    private String user;
    private String pass;
    private FileWriter log = null;

    private HttpClient client;
    private long fail = 0;
    private long success = 0;

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

    public void setLog(String log) {
        this.log = init(log);
    }

    private FileWriter init(String to) {
        try {
            return new FileWriter(to);
        } catch (IOException e) {
            LOG.error(to, e);
        }
        return null;
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
            log.append(response.body());
            log.append("\n");
            if (response.statusCode() == 200) {
                success++;
            } else {
                fail++;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void resetStream() {
        fail = 0;
        success = 0;
    }

    @Override
    public void closeStream() {
        if (log != null) {
            try {
                log.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        LOG.debug("Success: {}, Fail: {}", success, fail);
    }

}
