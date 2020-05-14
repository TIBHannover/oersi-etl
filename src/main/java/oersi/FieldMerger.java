package oersi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Merge map entries from fields with identical names under a single field.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class FieldMerger extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = Logger.getLogger(FieldMerger.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();

    static class MultiMap extends HashMap<String, Object> {
        private static final long serialVersionUID = 490682490432334605L;

        MultiMap() {
            // default constructor for Jackson
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object put(String key, Object value) {
            Object oldValue = get(key);
            if (oldValue instanceof Map && value instanceof Map) {
                Map<String, Object> map = ((Map<String, Object>) oldValue);
                map.putAll((Map<? extends String, ? extends Object>) value);
                return super.put(key, map);
            }
            return super.put(key, value);
        }
    }

    @Override
    public void process(final String s) {
        try {
            Map<String, Object> json = mapper.readValue(s, MultiMap.class);
            getReceiver().process(mapper.writeValueAsString(json));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
