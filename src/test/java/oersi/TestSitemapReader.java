/*
 * Copyright 2020 Fabian Steeg, hbz
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
 * Tests for {@link SitemapReader}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestSitemapReader {

    private static final String SITEMAP_URL = "https://www.oerbw.de/edu-sharing/eduservlet/sitemap?from=0";
    private static final String BASE = "https://uni-tuebingen.oerbw.de/edu-sharing/components/render/";
    private static final String ON_PAGE_1 = BASE + "651396b4-db24-44e4-a0f6-76aff3293f90"; // from=0
    private static final String ON_PAGE_2 = BASE + "605a7ed8-d318-4a2c-8c25-2a5985c85e15"; // from=500

    private SitemapReader sitemapReader;

    @Mock
    private ObjectReceiver<String> receiver;
    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sitemapReader = new SitemapReader();
        sitemapReader.setLimit(-1);
        sitemapReader.setWait(0);
        sitemapReader.setReceiver(receiver);
        inOrder = Mockito.inOrder(receiver);
        sitemapReader.process(SITEMAP_URL);
    }

    @Test
    public void testShouldProcessPage1() {
        inOrder.verify(receiver, Mockito.calls(1)).process(ON_PAGE_1);
    }

    @Test
    public void testShouldProcessPage2() {
        inOrder.verify(receiver, Mockito.calls(1)).process(ON_PAGE_2);
    }

    @After
    public void cleanup() {
        sitemapReader.closeStream();
    }
}
