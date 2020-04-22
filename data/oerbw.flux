"https://www.oerbw.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| indexer.SitemapReader(wait="1000",limit="2",urlPattern=".*/components/.*")
| open-http
| extract-script
| decode-json
| org.metafacture.metamorph.Metafix("
map(name, title)
map(description, description)
map(url,url)
map(identifier,id)")
| encode-json(prettyPrinting="false")
| json-to-elasticsearch-bulk(idKey="id",type="oerbw",index="oersi")
| write(FLUX_DIR + "oerbw.ndjson");
