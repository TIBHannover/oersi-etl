package oersi;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.joox.JOOX.matchText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.joox.JOOX;
import org.joox.Match;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

@RunWith(Parameterized.class)
public class EtlTestSiteMap {

    private static final Object[][] PARAMS = new Object[][] { //
            { "/hoou-sitemap.xml", "https://www.hoou.de/materials/", Arrays.asList( // -->
                    "https://www.hoou.de/materials/forschungsfrage-finden-trickfilme-zum-prozess", //
                    "https://www.hoou.de/materials/die-trompete-als-orchester-und-soloinstrument", //
                    "https://www.hoou.de/materials/stadt-als-sozialer-raum") }, //
            { "/oncampus-sitemap.xml", "https://www.oncampus.de/", Arrays.asList( // -->
                    "https://www.oncampus.de/Customer_Experience_Management", //
                    "https://www.oncampus.de/Leben+in+Deutschland", //
                    "https://www.oncampus.de/Propädeutik_Mathe_Grundlagen") } //
    };

    @Parameterized.Parameters(name = "{0}, {1} -> {2}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String sitemap;
    private String prefix;
    private List<String> urls;

    public EtlTestSiteMap(String sitemap, String prefix, List<String> titles) {
        this.sitemap = sitemap;
        this.prefix = prefix;
        this.urls = titles;
    }

    @Test
    public void testConvertSitemap() throws IOException, SAXException {
        List<String> resultUrls = readSiteMap(sitemap, prefix);
        assertFalse("Result URLs should not be empty", resultUrls.isEmpty());
        urls.forEach(url -> assertThat(resultUrls, hasItem(url)));
    }

    private List<String> readSiteMap(String sitemap, String prefix)
            throws SAXException, IOException {
        Match siteMapXml = JOOX.$(getClass().getResourceAsStream(sitemap));
        return siteMapXml.find("loc").filter(matchText("^" + prefix + ".*")).texts();
    }

}
