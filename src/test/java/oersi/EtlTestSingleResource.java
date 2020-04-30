package oersi;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.metafacture.runner.Flux;

@RunWith(Parameterized.class)
public class EtlTestSingleResource {

    private static final Logger LOG = Logger.getLogger(EtlTestSingleResource.class.getName());

    private static final Object[][] PARAMS = new Object[][] { //
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
                    "https://creativecommons.org/licenses/by-nc-sa/4.0/" } };

    @Parameterized.Parameters(name = "{0} -> {1}")
    public static Collection<Object[]> singleResources() {
        return Arrays.asList(PARAMS);
    }

    private String url;
    private String title;
    private String description;
    private String license;

    public EtlTestSingleResource(String url, String title, String description, String license) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.license = license;
    }

    @Test
    public void testConvertSingleResource() throws IOException, RecognitionException {
        String fix = "\n"//
                + "map(html.head.title.value, title)\n"//
                + "map(html.body.div.div.div.div.div.div.div.p.value, description)\n"//
                + "map(html.body.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.p.a.href, license)"
                + "\n";
        String out = absPathToTempFile("", ".json");
        String flux = String.format("\"%s\""//
                + "|open-http"//
                + "|decode-html"//
                + "|org.metafacture.metamorph.Metafix(\"%s\")"//
                + "|encode-json(prettyPrinting=\"false\")"//
                + "|write(\"%s\");", url, fix, out);
        LOG.log(Level.INFO, "Running Flux: {0}", flux);
        String json = convertSingleResource(url, flux, out);
        assertThat(json, containsString(title));
        assertThat(json, containsString(description));
        assertThat(json, containsString(license));
        assertThat(json, containsString(url));
    }

    private String convertSingleResource(String sourceUrl, String flux, String outFile)
            throws IOException, RecognitionException {
        Flux.main(new String[] { absPathToTempFile(flux, ".flux") });
        String json = Files.readAllLines(Paths.get(outFile)).stream().collect(Collectors.joining("\n"));
        // workaround until we can pick out a specific head.meta.content
        // (see https://github.com/metafacture/metafacture-fix/issues/10)
        return json.trim().replaceAll("\"}$", "\",\"url\" : \"" + sourceUrl + "\"}");
    }

    private String absPathToTempFile(String content, String suffix) throws IOException {
        File file = File.createTempFile("oerindex", suffix);
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.WRITE);
        return file.getAbsolutePath();
    }
}
