package oersi;

import java.io.FileWriter;
import java.io.IOException;

import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * Validate JSON against a given schema, pass only valid input to the receiver.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class JsonValidator extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonValidator.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonSchema schema;
    private long fail = 0;
    private long success = 0;
    private FileWriter writeInvalid = null;
    private FileWriter writeValid = null;

    public JsonValidator(final String url) {
        try {
            schema = JsonSchemaFactory.byDefault().getJsonSchema(url);
        } catch (ProcessingException e) {
            LOG.error(url, e);
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
            LOG.error(to, e);
        }
        return null;
    }

    @Override
    public void process(final String json) {
        try {
            JsonNode node = mapper.readTree(json);
            ProcessingReport report = schema.validate(node);
            if (report.isSuccess()) {
                getReceiver().process(json);
                success++;
                write(json, writeValid);
            } else {
                LOG.error("Invalid JSON: {}:\n{}", report, json);
                fail++;
                write(json, writeInvalid);
            }
        } catch (IOException | ProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void write(final String json, FileWriter to) throws IOException {
        if (to != null) {
            to.append(json);
            to.append("\n");
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
                LOG.error(e.getMessage(), e);
            }
        }
    }

}
