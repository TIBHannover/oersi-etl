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
import java.util.List;

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

/**
 * Tests for {@link NodeSearchReader}.
 *
 * @author Fabian Steeg
 *
 */
@RunWith(Parameterized.class)
public final class TestNodeSearchReader {

    // TODO: mock client like in TestOersiWriter, respond with local file
    private static final Object[][] PARAMS = new Object[][] { //
            { "https://www.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-?query=*&maxItems=10&propertyFilter=-all-&skipCount=0",
                    Arrays.asList( // -->
                            "https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/0a155013-e459-41b1-a9a8-3016783a5b95/metadata?propertyFilter=-all-", //
                            "https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/093aece8-41ff-46fb-887a-0d2049aa9e7e/metadata?propertyFilter=-all-", //
                            "https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/d97013a3-525c-40cb-a19b-f383536f9100/metadata?propertyFilter=-all-") } //
    };
    private String pattern = "^(.*)`https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-";

    @Parameterized.Parameters(name = "{0} -> {1}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String sitemap;
    private List<String> urls;

    public TestNodeSearchReader(String sitemap, List<String> titles) {
        this.sitemap = sitemap;
        this.urls = titles;
    }

    private NodeSearchReader reader;

    @Mock
    private ObjectReceiver<String> receiver;
    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        reader = new NodeSearchReader();
        reader.setLimit(25);
        reader.setWait(0);
        reader.setFindAndReplace(pattern);
        reader.setReceiver(receiver);
        inOrder = Mockito.inOrder(receiver);
    }

    @Test
    public void testShouldProcess() {
        reader.process(sitemap);
        for (String url : urls) {
            inOrder.verify(receiver, Mockito.calls(1)).process(url);
        }
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchInvalidUrl() {
        reader.process("");
    }

    @After
    public void cleanup() {
        reader.closeStream();
    }
}
