package oersi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Merge map entries from fields with identical names under a single field.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class FieldMerger extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private final ObjectMapper mapper;

    public FieldMerger() {
        mapper = new ObjectMapper();
        mapper.setNodeFactory(new JsonNodeFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public TextNode textNode(String text) {
                return super.textNode(text.trim());
            }
        });
    }

    static class MultiMap extends HashMap<String, Object> {
        private static final long serialVersionUID = 490682490432334605L;

        MultiMap() {
            // default constructor for Jackson
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object put(String key, Object value) {
            Object oldValue = get(key);
            Object newValue = value;
            if (oldValue instanceof Map && value instanceof Map) {
                Map<String, Object> map = ((Map<String, Object>) oldValue);
                map.putAll((Map<? extends String, ? extends Object>) value);
                newValue = map;
            } else if (oldValue instanceof List && value instanceof List) {
                List<?> oldValueAsList = (List<?>) oldValue;
                List<?> valueAsList = (List<?>) value;
                if (!oldValueAsList.isEmpty() && oldValueAsList.get(0) instanceof Map
                        && !valueAsList.isEmpty() && valueAsList.get(0) instanceof Map) {
                    Map<String, Object> map = ((Map<String, Object>) oldValueAsList.get(0));
                    map.putAll((Map<? extends String, ? extends Object>) valueAsList.get(0));
                    newValue = oldValueAsList;
                }
            }
            return super.put(key,
                    newValue instanceof List ? deduplicate((List<?>) newValue) : newValue);
        }

        private List<?> deduplicate(List<?> newValue) {
            return newValue.stream().distinct().collect(Collectors.toList());
        }
    }

    @Override
    public void process(final String s) {
        try {
            Map<String, Object> json = mapper.readValue(s, MultiMap.class);
            JsonNode tree = mapper.readTree(mapper.writeValueAsString(json));
            getReceiver().process(mapper.writeValueAsString(tree));
        } catch (IOException e) {
            throw new MetafactureException(e.getMessage(), e);
        }
    }
}
