package oersi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;

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

    private static final Logger LOG = Logger.getLogger(JsonValidator.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonSchema schema;

    public JsonValidator(final String url) {
        try {
            schema = JsonSchemaFactory.byDefault().getJsonSchema(url);
        } catch (ProcessingException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void process(final String json) {
        try {
            JsonNode node = mapper.readTree(json);
            ProcessingReport report = schema.validate(node);
            if (report.isSuccess()) {
                getReceiver().process(json);
            } else {
                LOG.log(Level.SEVERE, "Invalid JSON: {0} in:\n{1}", new Object[] { report, json });
            }
        } catch (IOException | ProcessingException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
