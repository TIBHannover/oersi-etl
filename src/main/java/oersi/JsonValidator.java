package oersi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate JSON against a given schema, pass only valid input to the receiver.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class JsonValidator extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonValidator.class);
    private Schema schema;
    private long fail = 0;
    private long success = 0;
    private FileWriter writeInvalid = null;
    private FileWriter writeValid = null;

    public JsonValidator(final String url, final String schemaResolutionScope) {
        try (InputStream inputStream = new URL(url).openStream()) {
            schema = SchemaLoader.builder()//
                    .schemaJson(new JSONObject(new JSONTokener(inputStream)))//
                    .schemaClient(SchemaClient.classPathAwareClient())//
                    .resolutionScope(schemaResolutionScope)//
                    .build().load().build();
        } catch (IOException | JSONException e) {
            throw new MetafactureException(e.getMessage(), e);
        }
    }

    public void setWriteInvalid(final String to) {
        writeInvalid = init(to);
    }

    public void setWriteValid(final String to) {
        writeValid = init(to);
    }

    private FileWriter init(String to) {
        try {
            return new FileWriter(to);
        } catch (IOException e) {
            throw new MetafactureException(e.getMessage(), e);
        }
    }

    @Override
    public void process(final String json) {
        JSONObject object = null;
        try {
            object = new JSONObject(json); // throws JSONException on syntax error
        } catch (JSONException e) {
            handleInvalid(json, null, e.getMessage());
        }
        try {
            schema.validate(object); // throws ValidationException if invalid
            getReceiver().process(json);
            success++;
            write(json, writeValid);
        } catch (ValidationException e) {
            handleInvalid(json, object, e.getAllMessages().toString());
        }
    }

    private void handleInvalid(final String json, final JSONObject object,
            final String errorMessage) {
        LOG.info("Invalid JSON: {} in {}", errorMessage, object != null ? object.opt("id") : json);
        fail++;
        write(json, writeInvalid);
    }

    private void write(final String json, FileWriter to) {
        if (to != null) {
            try {
                to.append(json);
                to.append("\n");
            } catch (IOException e) {
                throw new MetafactureException(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onCloseStream() {
        close(writeInvalid);
        close(writeValid);
        LOG.debug("Success: {}, Fail: {}", success, fail);
        super.onCloseStream();
    }

    private void close(FileWriter fw) {
        if (fw != null) {
            try {
                fw.close();
            } catch (IOException e) {
                throw new MetafactureException(e.getMessage(), e);
            }
        }
    }

}
