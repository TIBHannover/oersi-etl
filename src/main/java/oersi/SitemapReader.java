package oersi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private final Map<String, String> headers = new HashMap<>();

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

    public void setHeader(final String header) {
        Arrays.stream(Pattern.compile("\n").split(header)).forEach(h -> {
            final String[] parts = Pattern.compile(":").split(h, 2);
            if (parts.length == 2) {
                headers.put(parts[0].toLowerCase(), parts[1].trim());
            } else {
                throw new IllegalArgumentException("Invalid header: " + h);
            }
        });
    }

    @Override
    public void process(final String sitemap) {
        LOG.debug("Processing sitemap URL {}", sitemap);
        try {
            URLConnection urlConnection = new URL(sitemap).openConnection();
            headers.forEach(urlConnection::addRequestProperty);
            Match siteMapXml = JOOX.$(urlConnection.getInputStream());
            List<String> texts = siteMapXml.find("loc")
                    .map(m -> m.element().getTextContent().trim()).stream()
                    .filter(s -> s.matches(urlPattern)).collect(Collectors.toList());
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
