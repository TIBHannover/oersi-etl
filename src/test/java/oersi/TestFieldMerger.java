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
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Tests for {@link FieldMerger}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestFieldMerger {

    private FieldMerger fieldMerger;

    @Mock
    private ObjectReceiver<String> receiver;
    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        fieldMerger = new FieldMerger();
        fieldMerger.setReceiver(receiver);
        inOrder = Mockito.inOrder(receiver);
    }

    @Test
    public void testShouldProcessMaps() throws JsonProcessingException {
        fieldMerger.process(json("{'key':{'key1':'val1'},'key':{'key2':'val2'}}"));
        inOrder.verify(receiver).process(json("{'key':{'key1':'val1','key2':'val2'}}"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testShouldProcessMapsInArrays() throws JsonProcessingException {
        fieldMerger.process(json("{'key':[{'key1':'val1'}],'key':[{'key2':'val2'}]}"));
        inOrder.verify(receiver).process(json("{'key':[{'key1':'val1','key2':'val2'}]}"));
    }

    private String json(String string) {
        return string.replace("'", "\"");
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchInvalidJson() {
        fieldMerger.process("{");
    }

    @After
    public void cleanup() {
        fieldMerger.closeStream();
    }
}
