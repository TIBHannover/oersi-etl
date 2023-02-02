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
 * Tests for {@link SitemapReader}.
 *
 * @author Fabian Steeg
 *
 */
@RunWith(Parameterized.class)
public final class TestSitemapReader {

    private static final Object[][] PARAMS = new Object[][] { //
            { "/hoou-sitemap.xml", "https://www.hoou.de/materials/", Arrays.asList( // -->
                    "https://www.hoou.de/materials/forschungsfrage-finden-trickfilme-zum-prozess", //
                    "https://www.hoou.de/materials/die-trompete-als-orchester-und-soloinstrument", //
                    "https://www.hoou.de/materials/stadt-als-sozialer-raum") }, //
            { "/oncampus-sitemap.xml", "https://www.oncampus.de/", Arrays.asList( // -->
                    "https://www.oncampus.de/Customer_Experience_Management", //
                    "https://www.oncampus.de/Leben+in+Deutschland", //
                    "https://www.oncampus.de/Propädeutik_Mathe_Grundlagen") }, //
            { "/oerbw-sitemap.xml", "https://uni-tuebingen.oerbw.de/", Arrays.asList( // -->
                    "https://uni-tuebingen.oerbw.de/edu-sharing/components/render/907ca040-2d0c-4a89-a122-177d02df7685", //
                    "https://uni-tuebingen.oerbw.de/edu-sharing/components/render/76bd5ef6-8607-4294-8a8c-2494aeff5ee9", //
                    "https://uni-tuebingen.oerbw.de/edu-sharing/components/render/e65bb0f8-c30d-4205-9dcf-a99d372c2012") } //
    };

    @Parameterized.Parameters(name = "{0}, {1} -> {2}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String sitemap;
    private String prefix;
    private List<String> urls;

    public TestSitemapReader(String sitemap, String prefix, List<String> titles) {
        this.sitemap = sitemap;
        this.prefix = prefix;
        this.urls = titles;
    }

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
        sitemapReader.setUrlPattern("^" + prefix + ".*");
        sitemapReader.setReceiver(receiver);
        inOrder = Mockito.inOrder(receiver);
    }

    @Test
    public void testShouldProcess() {
        sitemapReader.process(getClass().getResource(sitemap).toString());
        for (String url : urls) {
            inOrder.verify(receiver, Mockito.calls(1)).process(url);
        }
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchInvalidUrl() {
        sitemapReader.process("");
    }

    @After
    public void cleanup() {
        sitemapReader.closeStream();
    }
}
