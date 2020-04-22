package indexer;

import static org.joox.JOOX.matchText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.antlr.runtime.RecognitionException;
import org.joox.JOOX;
import org.joox.Match;
import org.metafacture.runner.Flux;
import org.xml.sax.SAXException;

public class Indexer {

    static final File DATA_DIR = new File("data");
    static final File OUT_FILE = new File(DATA_DIR, "oersi.ndjson");

    private static final Logger LOG = Logger.getLogger(Indexer.class.getName());

    public static void main(String[] args) throws IOException {
        try {
            for (File flux : DATA_DIR.listFiles((d, f) -> f.toLowerCase().endsWith(".flux"))) {
                String fluxText = Files.readAllLines(Paths.get(flux.toURI())).stream()
                        .collect(Collectors.joining("\n"));
                LOG.log(Level.INFO, "Running {0}: \n{1}", new Object[] { flux, fluxText });
                Flux.main(new String[] { flux.getAbsolutePath() });
            }
            try (FileWriter w = new FileWriter(OUT_FILE)) {
                for (File json : DATA_DIR.listFiles(
                        (d, f) -> f.toLowerCase().endsWith(".ndjson") && !f.equalsIgnoreCase(OUT_FILE.getName()))) {
                    LOG.log(Level.INFO, "Writing {0} to {1}", new Object[] { json, OUT_FILE });
                    String bulk = Files.readAllLines(Paths.get(json.toURI())).stream()
                            .collect(Collectors.joining("\n"));
                    w.write(bulk);
                    w.write("\n");
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String convertSingleResource(String sourceUrl, String flux, String outFile)
            throws IOException, RecognitionException {
        Flux.main(new String[] { absPathToTempFile(flux, ".flux") });
        String json = Files.readAllLines(Paths.get(outFile)).stream().collect(Collectors.joining("\n"));
        // workaround until we can pick out a specific head.meta.content
        // (see https://github.com/metafacture/metafacture-fix/issues/10)
        return json.trim().replaceAll("\"}$", "\",\"url\" : \"" + sourceUrl + "\"}");
    }

    public List<String> readSiteMap(String sitemapUrl, String prefix) throws SAXException, IOException {
        Match siteMapXml = JOOX.$(new URL(sitemapUrl).openStream());
        return siteMapXml.find("loc").filter(matchText("^" + prefix + ".*")).texts();
    }

    public String absPathToTempFile(String content, String suffix) throws IOException {
        File file = File.createTempFile("oerindex", suffix);
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.WRITE);
        return file.getAbsolutePath();
    }

}
