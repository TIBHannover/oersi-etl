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

import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.ObjectReceiver;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Charsets;

/**
 * Tests for {@link JsonValidator}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestJsonValidator {
    private static final String SCHEMA = "https://raw.githubusercontent.com/oersi/lrmi-profile/master/draft/schemas/schema.json";
    private static final String JSON_INVALID = "{\"key\":\"val\"}";
    private static final String JSON_VALID = fromUrl(
            "https://raw.githubusercontent.com/oersi/lrmi-profile/develop/draft/examples/valid/mainEntityOf.json");
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

    private static String fromUrl(String url) {
        try {
            return new String(new URL(url).openStream().readAllBytes(), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    @After
    public void cleanup() {
        validator.closeStream();
    }
}
