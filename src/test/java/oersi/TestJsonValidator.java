/*
 * Copyright 2021 Fabian Steeg, hbz
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oersi;

import java.util.Arrays;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

    private static final String SCHEMA = "resource:/schemas/schema.json";
    private static final Map<String, Object> JSON_INVALID = ImmutableMap.of("key", "val");
    private static final Map<String, Object> JSON_VALID = ImmutableMap.of(//
            "id", "https://example.org/oer", //
            "name", "Beispielkurs", //
            "@context", Arrays.asList( //
                    "https://w3id.org/kim/lrmi-profile/draft/context.jsonld", //
                    ImmutableMap.of("@language", "de")),
            "type", Arrays.asList("LearningResource"), //
            "mainEntityOfPage", Arrays.asList(ImmutableMap.of( //
                    "id", "https://example.org/oer-description.html", //
                    "provider", ImmutableMap.of(//
                            "id", "https://example.org/oer-provider.html", //
                            "name", "example"))));
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonValidator validator;

    @Mock
    private ObjectReceiver<String> receiver;
    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        validator = new JsonValidator(SCHEMA);
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
    public void testShouldInvalidate() throws JsonProcessingException {
        validator.process(MAPPER.writeValueAsString(JSON_INVALID));
        inOrder.verifyNoMoreInteractions();
    }

    @After
    public void cleanup() {
        validator.closeStream();
    }
}
