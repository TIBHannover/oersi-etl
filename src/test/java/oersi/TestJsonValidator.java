package oersi;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * Tests for {@link JsonValidator}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestJsonValidator {

    private static final String SCHEMA = "https://gitlab.com/oersi/oersi-schema/-/raw/main/schemas/metadata/schema.json";
    private static final String SCHEMA_RESOLUTION_SCOPE = "https://gitlab.com/oersi/oersi-schema/-/raw/main/schemas/metadata/";
    private static final List<Serializable> CONTEXT = Arrays.asList( //
            "https://w3id.org/kim/amb/draft/context.jsonld", //
            ImmutableMap.of("@language", "de"));
    private static final ImmutableMap<String, String> PROVIDER = ImmutableMap.of(//
            "id", "https://example.org/oer-provider.html", //
            "name", "example");
    private static final Map<String, Object> JSON_INVALID_SHORT = ImmutableMap.of("key", "val");
    private static final String JSON_INVALID_DUPLICATE_KEY = "{\"key\":\"val\",\"key\":\"val\"}";
    private static final String JSON_INVALID_SYNTAX_ERROR = "{\"key1\":\"val\",\"key2\":\"val\"";
    private static final Map<String, Object> JSON_INVALID_LONG = ImmutableMap.of(//
            "id", "https://example.org/oer", //
            "name", "Beispielkurs", //
            "@context", CONTEXT, //
            "type", Arrays.asList("LearningResource"), //
            "mainEntityOfPage", Arrays.asList(ImmutableMap.of( //
                    "id", "https://example.org/oer-description.html", //
                    "dateModified", "June 2022", //
                    "provider", PROVIDER)));
    private static final Map<String, Object> JSON_VALID = ImmutableMap.of(//
            "id", "https://example.org/oer", //
            "name", "Beispielkurs", //
            "@context", CONTEXT, "type", //
            Arrays.asList("LearningResource"), //
            "mainEntityOfPage", Arrays.asList(ImmutableMap.of( //
                    "id", "https://example.org/oer-description.html", //
                    "dateModified", "2022-06-10", //
                    "provider", PROVIDER)));
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonValidator validator;

    @Mock
    private ObjectReceiver<String> receiver;
    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        validator = new JsonValidator(SCHEMA, SCHEMA_RESOLUTION_SCOPE);
        validator.setReceiver(receiver);
        inOrder = Mockito.inOrder(receiver);
    }

    @Test
    public void testShouldValidate() throws JsonProcessingException {
        String valid = MAPPER.writeValueAsString(JSON_VALID);
        validator.process(valid);
        inOrder.verify(receiver, Mockito.calls(1)).process(valid);
    }

    @Test
    public void testShouldInvalidateShort() throws JsonProcessingException {
        validator.process(MAPPER.writeValueAsString(JSON_INVALID_SHORT));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testShouldInvalidateDuplicateKey() throws JsonProcessingException {
        validator.process(JSON_INVALID_DUPLICATE_KEY);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testShouldInvalidateSyntaxError() throws JsonProcessingException {
        validator.process(JSON_INVALID_SYNTAX_ERROR);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testShouldInvalidateLong() throws JsonProcessingException {
        validator.process(MAPPER.writeValueAsString(JSON_INVALID_LONG));
        inOrder.verifyNoMoreInteractions();
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchMissingSchemaFile() throws JsonProcessingException {
        new JsonValidator("", "");
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchMissingOutputFile() throws JsonProcessingException {
        validator.setWriteValid("");
        validator.process(MAPPER.writeValueAsString(JSON_INVALID_SHORT));
    }

    @After
    public void cleanup() {
        validator.closeStream();
    }
}
