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
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
@RunWith(Parameterized.class)
public final class TestFieldMerger {

    private static final Object[][] PARAMS = new Object[][] { //
            { "Process-Maps", //
                    json("{'key':{'key1':'val1'},'key':{'key2':'val2'}}"), //
                    json("{'key':{'key1':'val1','key2':'val2'}}") }, //
            { "Process-Maps-In-Arrays", //
                    json("{'key':[{'key1':'val1'}],'key':[{'key2':'val2'}]}"), //
                    json("{'key':[{'key1':'val1','key2':'val2'}]}") }, //
            { "Deduplicate-Maps-In-Arrays", //
                    json("{'key':[{'key1':'val1'},{'key1':'val1'}]}"), //
                    json("{'key':[{'key1':'val1'}]}") }, //
            { "Deduplicate-Maps-In-Merged-Arrays", //
                    json("{'key':[{'key1':'val1'}],'key':[{'key1':'val1'}]}"), //
                    json("{'key':[{'key1':'val1'}]}") },
            { "Retain-Order-In-Mixed-Arrays", //
                    json("{'key':['https',{'key1':'val1'}]}"), //
                    json("{'key':['https',{'key1':'val1'}]}") } };

    @Parameterized.Parameters(name = "{0}, {1} -> {2}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String in;
    private String out;

    public TestFieldMerger(String name, String in, String out) {
        this.in = in;
        this.out = out;
    }

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
    public void testProcess() throws JsonProcessingException {
        fieldMerger.process(in);
        inOrder.verify(receiver).process(out);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchInvalidJson() {
        fieldMerger.process("{" + in);
    }

    @After
    public void cleanup() {
        fieldMerger.closeStream();
    }

    private static String json(String string) {
        return string.replace("'", "\"");
    }

}
