"file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait="0", limit="1", urlPattern=".*/(somethingthatdoesnotexist)/.*")
| object-to-literal(literalName="id", recordId="%d")
| encode-json
| print
;