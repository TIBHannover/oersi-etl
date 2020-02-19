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
import java.util.UUID;
import java.util.stream.Collectors;

import org.antlr.runtime.RecognitionException;
import org.joox.JOOX;
import org.joox.Match;
import org.metafacture.runner.Flux;
import org.xml.sax.SAXException;

public class Indexer {

    public static void main(String[] args) throws IOException, SAXException {
        String siteMapUrl = "https://www.hoou.de/sitemap.xml";
        String prefix = "https://www.hoou.de/materials/";
        Indexer indexer = new Indexer();
        String fix = "\n"//
                + "map(html.head.title.value, title)\n"//
                + "map(html.body.div.div.div.div.div.div.div.p.value, description)\n"//
                + "map(html.body.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.p.a.href, license)"
                + "\n";
        int num = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        File resultFile = new File("elasticsearch-bulk.ndjson");
        List<String> urls = indexer.readSiteMap(siteMapUrl, prefix).subList(0, num);
        System.out.printf("Processing %s resources from %s: %s, writing to %s\n", num, siteMapUrl, urls, resultFile);
        try (FileWriter out = new FileWriter(resultFile)) {
            urls.forEach(url -> {
                try {
                    String singleResult = indexer.absPathToTempFile("", ".json");
                    String flux = String.format("\"%s\""//
                            + "|open-http"//
                            + "|decode-html"//
                            + "|org.metafacture.metamorph.Metafix(\"%s\")"//
                            + "|encode-json(prettyPrinting=\"false\")"//
                            + "|write(\"%s\");", url, fix, singleResult);
                    out.write(String.format("{\"index\":{\"_id\":\"%s\",\"_type\":\"%s\",\"_index\":\"%s\"}}",
                            // _id, _type, _index:
                            UUID.randomUUID(), "oer", "oerindex"));
                    out.write("\n");
                    out.write(indexer.convertSingleResource(url, flux, singleResult));
                    out.write("\n");
                    Thread.sleep(1000);
                } catch (IOException | RecognitionException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
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
