package oersi;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;

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
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V7);
        try {
            schema = factory.getSchema(new URL(url).openStream());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void process(final String json) {
        try {
            JsonNode node = mapper.readTree(json);
            Set<ValidationMessage> errors = schema.validate(node);
            if (errors.isEmpty()) {
                getReceiver().process(json);
            } else {
                LOG.log(Level.SEVERE, "Invalid JSON: {0} in:\n{1}", new Object[] { errors, json });
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
