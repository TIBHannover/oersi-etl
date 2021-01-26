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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.ObjectReceiver;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link JsonValidator}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestJsonValidator {

    private static final String SCHEMA = "https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json";
    private static final String JSON_INVALID = "{\"key\":\"val\"}";
    private static final String JSON_VALID = "{\"id\":\"https://example.org/oer\","
            + "\"name\": \"Beispielkurs\", "
            + "\"@context\": [\"https://w3id.org/kim/lrmi-profile/draft/context.jsonld\", {\"@language\": \"de\"}],"
            + "\"type\": [\"LearningResource\"]}";

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
    public void testShouldValidate() {
        validator.process(JSON_VALID);
        inOrder.verify(receiver, Mockito.calls(1)).process(JSON_VALID);
    }

    @Test
    public void testShouldInvalidate() {
        validator.process(JSON_INVALID);
        inOrder.verifyNoMoreInteractions();
    }

    @After
    public void cleanup() {
        validator.closeStream();
    }
}
