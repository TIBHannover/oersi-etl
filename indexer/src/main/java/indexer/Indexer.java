package indexer;

import static org.joox.JOOX.matchText;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.RecognitionException;
import org.joox.JOOX;
import org.joox.Match;
import org.metafacture.runner.Flux;
import org.xml.sax.SAXException;

public class Indexer {
    public String convertSingleResource(String sourceUrl, String flux, String outFile)
            throws IOException, RecognitionException {
        Flux.main(new String[] { absPathToTempFile(flux, ".flux") });
        String json = Files.readAllLines(Paths.get(outFile)).stream().collect(Collectors.joining("\n"));
        // workaround until we can pick out a specific head.meta.content
        // (see https://github.com/metafacture/metafacture-fix/issues/10)
        return json.trim().replaceAll("\"\n}$", "\",\n  \"url\" : \"" + sourceUrl + "\"\n}");
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
