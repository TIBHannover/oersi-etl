package oersi;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.metafacture.framework.MetafactureException;
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
    private int bulkSize = 25;
    private JSONArray bulk = new JSONArray();
    private FileWriter logWriter = null;

    HttpClient client;
    long fail = 0;
    long success = 0;

    public OersiWriter(final String url) {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass.toCharArray());
            }
        };
        client = HttpClient.newBuilder().authenticator(auth).build();
        this.url = url + "/bulk";
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setBulkSize(int bulkSize) {
        this.bulkSize = bulkSize;
    }

    public void setLog(String log) {
        this.logWriter = init(log);
    }

    private FileWriter init(String to) {
        try {
            return new FileWriter(to);
        } catch (IOException e) {
            throw new MetafactureException(to, e);
        }
    }

    @Override
    public void process(final String obj) {
        if (bulk.length() < bulkSize) {
            bulk.put(new JSONObject(obj));
        }
        if (bulk.length() == bulkSize) {
            postBulk();
        }
    }

    @Override
    public void resetStream() {
        postBulk();
        fail = 0;
        success = 0;
    }

    @Override
    public void closeStream() {
        postBulk();
        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (IOException e) {
                throw new MetafactureException(e.getMessage(), e);
            }
        }
        LOG.debug("Success: {}, Fail: {}", success, fail);
    }

    private void postBulk() {
        if (bulk.length() == 0) {
            return;
        }
        String requestBody = bulk.toString();
        LOG.debug("Posting to {}: {}", url, requestBody);
        HttpRequest request = HttpRequest.newBuilder()//
                .uri(URI.create(url))//
                .setHeader("content-type", TYPE)//
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (logWriter != null) {
                logWriter.append(response.body());
                logWriter.append("\n");
            }
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                success += jsonResponse.getInt("success");
                fail += jsonResponse.getInt("failed");
            } else {
                fail += bulkSize;
            }
            bulk.clear();
        } catch (IOException e) {
            throw new MetafactureException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MetafactureException(e.getMessage(), e);
        }
    }
}
