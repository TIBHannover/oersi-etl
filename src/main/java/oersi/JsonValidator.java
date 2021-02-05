package oersi;

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

    public JsonValidator(final String url) {
        try {
            schema = JsonSchemaFactory.byDefault().getJsonSchema(url);
        } catch (ProcessingException e) {
            LOG.error(e.getMessage(), e);
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
                LOG.error("Invalid JSON: {}:\n{}", report, json);
            }
        } catch (IOException | ProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
