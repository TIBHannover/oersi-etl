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

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.io.HttpOpener;
import org.metafacture.io.HttpOpener.Method;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link JsonApiReader}.
 *
 * TODO: mock apis to avoid using network resources
 *
 * @author Fabian Steeg
 *
 */
@RunWith(Parameterized.class)
public final class TestJsonApiReader {

    private static final int LIMIT = 15;
    private static final Object[][] PARAMS = new Object[][] { //
            { "https://www.zoerr.de/edu-sharing/rest/search/v1/queriesV2/-home-/-default-/ngsearch?maxItems=10&skipCount=0&propertyFilter=-all-", //
                    Method.POST, "{\"criterias\":[],\"facettes\":[]}", "nodes", "skipCount=", 10 },
            { "https://open.umn.edu/opentextbooks/textbooks?page=1", //
                    Method.GET, null, "data", "page=", 1 } };

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String url;
    private Method method;
    private String body;
    private String recordPath;
    private String pageParam;
    private int stepSize;

    public TestJsonApiReader(String url, HttpOpener.Method method, String body, String recordPath,
            String pageParam, int stepSize) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.recordPath = recordPath;
        this.pageParam = pageParam;
        this.stepSize = stepSize;
    }

    @Mock
    private ObjectReceiver<String> receiver;
    @Captor
    private ArgumentCaptor<String> captor;
    private JsonApiReader reader;
    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        reader = new JsonApiReader();
        reader.setLimit(LIMIT);
        reader.setWait(0);
        reader.setMethod(method);
        reader.setBody(body);
        reader.setRecordPath(recordPath);
        reader.setPageParam(pageParam);
        reader.setStepSize(stepSize);
        reader.setReceiver(receiver);
        inOrder = Mockito.inOrder(receiver);
    }

    @Test
    public void testShouldProcess() {
        reader.process(url);
        inOrder.verify(receiver, Mockito.calls(LIMIT)).process(captor.capture());
        assertNotNull(new JSONObject(captor.getValue()));
    }
}
