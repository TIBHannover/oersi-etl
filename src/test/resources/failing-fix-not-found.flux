"file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait="0", limit="1", urlPattern=".*/(materials|projects)/.*")
| object-to-literal(literalName="id", recordId="%d")
| fix("no-such.fix")
| encode-json
| print
;
