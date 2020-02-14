package indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IndexerTest {

    @Parameterized.Parameters
    public static Collection<Object[]> singleResources() {
        return Arrays.asList(new Object[][] { //
                { "https://www.hoou.de/materials/tutorial-lernen-lernen",
                        // ->
                        "Tutorial: Lernen lernen - HOOU", //
                        "Das Bewusstsein und die Kenntnis über Ihren Lernstil kann Ihnen helfen, ", //
                        "https://creativecommons.org/licenses/by-nc-nd/4.0/" }, //
                { "https://www.hoou.de/materials/online-punkteabfrage",
                        // ->
                        "Online Punkteabfrage - HOOU", //
                        "Die Punktabfrage ist in Papierform (Poster / Klebepunkte) ein bewährtes Feedbackinstrument ", //
                        "http://creativecommons.org/licenses/by-sa/4.0/" }, //
                { "https://www.hoou.de/materials/ifm-erzahlung-wie",
                        // ->
                        "IFM - Erzählung. Wie? - HOOU", //
                        "In diesem Kapitel zeigt Professor Bramkamp anhand der Videoinstallation „Angels in Chains“ ", //
                        "https://creativecommons.org/licenses/by-nc-sa/4.0/" } });
    }

    private String url;
    private String title;
    private String description;
    private String license;

    public IndexerTest(String url, String title, String description, String license) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.license = license;
    }

    @Test
    public void testConvertSingleResource() throws IOException, RecognitionException {
        Indexer indexer = new Indexer();
        String fix = "\n"//
                + "map(html.head.title.value, title)\n"//
                + "map(html.body.div.div.div.div.div.div.div.p.value, description)\n"//
                + "map(html.body.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.p.a.href, license)"
                + "\n";
        String out = indexer.absPathToTempFile("", ".json");
        String flux = String.format("\"%s\""//
                + "|open-http"//
                + "|decode-html"//
                + "|org.metafacture.metamorph.Metafix(\"%s\")"//
                + "|encode-json(prettyPrinting=\"true\")" + "|write(\"%s\");", url, fix, out);
        System.out.println("Running Flux: " + flux);
        String json = indexer.convertSingleResource(url, flux, out);
        assertThat(json, containsString(title));
        assertThat(json, containsString(description));
        assertThat(json, containsString(license));
        assertThat(json, containsString(url));
    }
}
