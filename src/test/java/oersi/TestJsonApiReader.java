/*
 * Copyright 2022 Fabian Steeg, hbz
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.io.HttpOpener.Method;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link JsonApiReader}.
 *
 * TODO: mock or use local data to avoid using network resource, test results
 *
 * @author Fabian Steeg
 *
 */
@RunWith(Parameterized.class)
public final class TestJsonApiReader {

    private static final Object[][] PARAMS = new Object[][] { {
            "https://www.zoerr.de/edu-sharing/rest/search/v1/queriesV2/-home-/-default-/ngsearch?maxItems=10&skipCount=0&propertyFilter=-all-" } };

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String url;

    public TestJsonApiReader(String url) {
        this.url = url;
    }

    private JsonApiReader reader;

    @Mock
    private ObjectReceiver<String> receiver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        reader = new JsonApiReader();
        reader.setMethod(Method.POST);
        reader.setLimit(15);
        reader.setWait(0);
        reader.setBody("{\"criterias\": [], \"facettes\": []}");
        reader.setRecordPath("nodes");
        reader.setPageParam("skipCount=");
        reader.setStepSize(10);
        reader.setReceiver(receiver);
    }

    @Test
    public void testShouldProcess() {
        reader.process(url);
    }
}
