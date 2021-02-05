package oersi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Merge map entries from fields with identical names under a single field.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class FieldMerger extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(FieldMerger.class);

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
            if (oldValue instanceof List && value instanceof List) {
                List<?> oldValueAsList = (List<?>) oldValue;
                List<?> valueAsList = (List<?>) value;
                if (!oldValueAsList.isEmpty() && oldValueAsList.get(0) instanceof Map
                        && !valueAsList.isEmpty() && valueAsList.get(0) instanceof Map) {
                    Map<String, Object> map = ((Map<String, Object>) oldValueAsList.get(0));
                    map.putAll((Map<? extends String, ? extends Object>) valueAsList.get(0));
                    return super.put(key, oldValueAsList);
                }
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
            LOG.error(e.getMessage(), e);
        }
    }
}
