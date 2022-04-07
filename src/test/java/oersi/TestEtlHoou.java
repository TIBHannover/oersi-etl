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
import java.util.stream.Collectors;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class TestEtlHoou {

    private static final Logger LOG = LoggerFactory.getLogger(TestEtlHoou.class);

    private static final Object[][] PARAMS = new Object[][] { //
            { "/hoou-tutorial-lernen-lernen.html",
                    // ->
                    "Tutorial: Lernen lernen", //
                    "Das Bewusstsein und die Kenntnis über Ihren Lernstil kann Ihnen helfen, ", //
                    "https://creativecommons.org/licenses/by-nc-nd/4.0/" }, //
            { "/hoou-online-punkteabfrage.html",
                    // ->
                    "Online Punkteabfrage", //
                    "Die Punktabfrage ist in Papierform (Poster / Klebepunkte) ein bewährtes Feedbackinstrument ", //
                    "http://creativecommons.org/licenses/by-sa/4.0/" }, //
            { "/hoou-ifm-erzaehlung-wie.html",
                    // ->
                    "IFM - Erzählung. Wie?", //
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

    public TestEtlHoou(String url, String title, String description, String license) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.license = license;
    }

    @Test
    public void testConvertSingleResource() throws IOException, RecognitionException {
        String fix = "\n"//
                + "move_field(html.head.title.value, title)\n"//
                + "copy_field(_id, id)"//
                + "\n";
        String out = absPathToTempFile("", ".json");
        String flux = String.format("\"%s\""//
                + "|open-file"//
                + "|decode-html"//
                + "|fix(\"%s\")"//
                + "|encode-json(prettyPrinting=\"false\")"//
                + "|write(\"%s\");", getClass().getResource(url).getFile(), fix, out);
        LOG.info("Running Flux: {}", flux);
        String json = convertSingleResource(url, flux, out);
        assertThat(json, containsString(title));
        assertThat(json, containsString(description));
        assertThat(json, containsString(license));
        assertThat(json, containsString(url));
    }

    private String convertSingleResource(String sourceUrl, String flux, String outFile)
            throws IOException, RecognitionException {
        ETL.main(new String[] { absPathToTempFile(flux, ".flux") });
        String json = Files.readAllLines(Paths.get(outFile)).stream()
                .collect(Collectors.joining("\n"));
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
