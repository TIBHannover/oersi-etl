package indexer;

import static org.joox.JOOX.matchText;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joox.JOOX;
import org.joox.Match;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.xml.sax.SAXException;

/**
 * Reads a sitemap and emits URLs.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class SitemapReader extends DefaultObjectPipe<Reader, ObjectReceiver<String>> {

    private static final Logger LOG = Logger.getLogger(SitemapReader.class.getName());

    private String urlPattern = ".*";
    private int limit = 10;
    private int wait = 5000;

    public void setUrlPattern(final String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @Override
    public void process(final Reader sitemap) {
        try {
            Match siteMapXml = JOOX.$(sitemap);
            List<String> texts = siteMapXml.find("loc").filter(matchText(urlPattern)).texts();
            for (String url : texts.subList(0, Math.min(limit, texts.size()))) {
                LOG.log(Level.INFO, "Processing URL {0}", new Object[] { url });
                getReceiver().process(url);
                Thread.sleep(wait);
            }
        } catch (SAXException | IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
