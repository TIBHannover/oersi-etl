package oersi;

import static org.joox.JOOX.matchText;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
public final class SitemapReader extends DefaultObjectPipe<String, ObjectReceiver<String>> {

    private static final String REP_START = "{";
    private static final String REP_END = "}";

    private static final Logger LOG = Logger.getLogger(SitemapReader.class.getName());

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
    public void process(final String input) {
        List<String> urls = getSitemaps(input);
        try {
            for (String sitemap : urls) {
                LOG.log(Level.INFO, "Processing sitemap URL {0}", new Object[] { sitemap });
                Match siteMapXml = JOOX.$(new URL(sitemap));
                List<String> texts = siteMapXml.find("loc").filter(matchText(urlPattern)).texts();
                for (String url : texts.subList(0, Math.min(limit, texts.size()))) {
                    LOG.log(Level.INFO, "Processing resource URL {0}", new Object[] { url });
                    getReceiver().process(findAndReplace(url));
                    Thread.sleep(wait);
                }
            }
        } catch (SAXException | IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    // http://a.com/{b,c} -> [http://a.com/b, http://a.com/c]
    private List<String> getSitemaps(String sitemap) {
        List<String> urls = new ArrayList<String>();
        if (sitemap.contains(REP_START) && sitemap.contains(REP_END)) {
            String rep = sitemap.substring(sitemap.indexOf(REP_START) + 1,
                    sitemap.indexOf(REP_END));
            for (String elem : rep.split(",")) {
                urls.add(sitemap.replace(REP_START + rep + REP_END, elem.trim()));
            }
        } else {
            urls.add(sitemap);
        }
        return urls;
    }

    private String findAndReplace(String url) {
        if (findAndReplace != null) {
            String[] split = findAndReplace.split("`");
            return url.replaceAll(split[0], split[1]);
        }
        return url;
    }
}
