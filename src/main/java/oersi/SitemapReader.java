package oersi;

import static org.joox.JOOX.matchText;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.joox.JOOX;
import org.joox.Match;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Reads a sitemap and emits URLs.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class SitemapReader extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapReader.class);

    private String urlPattern = ".*";
    private int limit = Integer.MAX_VALUE;
    private int wait = 1000;

    private String findAndReplace;

    public void setUrlPattern(final String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setLimit(final int limit) {
        this.limit = limit < 0 ? Integer.MAX_VALUE : limit;
    }

    public void setFindAndReplace(final String findAndReplace) {
        this.findAndReplace = findAndReplace;
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @Override
    public void process(final String sitemap) {
        LOG.debug("Processing sitemap URL {}", sitemap);
        try {
            Match siteMapXml = JOOX.$(new URL(sitemap));
            List<String> texts = siteMapXml.find("loc").filter(matchText(urlPattern)).texts();
            for (String url : texts.subList(0, Math.min(limit, texts.size()))) {
                LOG.trace("Processing resource URL {}", url);
                getReceiver().process(findAndReplace(url));
                Thread.sleep(wait);
            }
            tryNextPage(sitemap, texts.size());
        } catch (SAXException | IOException e) {
            throw new MetafactureException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MetafactureException(e.getMessage(), e);
        }
    }

    private void tryNextPage(final String sitemap, int currentPageSize) {
        String fromParam = "from=";
        boolean pagingIsSupported = sitemap.contains(fromParam);
        boolean isDone = currentPageSize == 0 || limit <= currentPageSize;
        if (pagingIsSupported && !isDone) {
            try (Scanner scanner = new Scanner(
                    sitemap.substring(sitemap.indexOf(fromParam) + fromParam.length()))) {
                if (scanner.hasNextInt()) {
                    int lastFrom = scanner.nextInt();
                    int nextFrom = lastFrom + currentPageSize;
                    process(sitemap.replace(fromParam + lastFrom, fromParam + nextFrom));
                }
            }
        }
    }

    private String findAndReplace(String url) {
        if (findAndReplace != null) {
            String[] split = findAndReplace.split("`");
            return url.replaceAll(split[0], split[1]);
        }
        return url;
    }
}
