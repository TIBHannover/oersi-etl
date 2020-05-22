"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| open-http                       // open-file
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/materials/.*")
| open-http
| decode-html
| org.metafacture.metamorph.Metafix("
map(html.head.title.value, title)
map(html.body.div.div.div.div.div.div.div.p.value, description)
map(html.body.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.p.a.href, license)")
| encode-json
| json-to-elasticsearch-bulk(type="hoou",index="oersi")
| write(FLUX_DIR + "hoou.ndjson");
