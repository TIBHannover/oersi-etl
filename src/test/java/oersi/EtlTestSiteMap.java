package oersi;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.joox.JOOX.matchText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
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
            { "https://www.hoou.de/sitemap.xml", "https://www.hoou.de/materials/", Arrays.asList( // -->
                    "https://www.hoou.de/materials/tutorial-lernen-lernen", //
                    "https://www.hoou.de/materials/online-punkteabfrage", //
                    "https://www.hoou.de/materials/ifm-erzahlung-wie") }, //
            { "https://www.oncampus.de/sitemap.xml", "https://www.oncampus.de/", Arrays.asList( // -->
                    "https://www.oncampus.de/Customer_Experience_Management", //
                    "https://www.oncampus.de/Leben+in+Deutschland", //
                    "https://www.oncampus.de/Propädeutik_Mathe_Grundlagen") } //
    };

    @Parameterized.Parameters(name = "{0}, {1} -> {2}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String siteMapUrl;
    private String prefix;
    private List<String> urls;

    public EtlTestSiteMap(String siteMapUrl, String prefix, List<String> titles) {
        this.siteMapUrl = siteMapUrl;
        this.prefix = prefix;
        this.urls = titles;
    }

    @Test
    public void testConvertSitemap() throws IOException, SAXException {
        List<String> resultUrls = readSiteMap(siteMapUrl, prefix);
        assertFalse("Result URLs should not be empty", resultUrls.isEmpty());
        urls.forEach(url -> assertThat(resultUrls, hasItem(url)));
    }

    private List<String> readSiteMap(String sitemapUrl, String prefix) throws SAXException, IOException {
        Match siteMapXml = JOOX.$(new URL(sitemapUrl).openStream());
        return siteMapXml.find("loc").filter(matchText("^" + prefix + ".*")).texts();
    }

}