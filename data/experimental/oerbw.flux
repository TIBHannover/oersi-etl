"https://www.oerbw.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/components/.*")
| open-http
| extract-script
| decode-json
| fix(FLUX_DIR+"edu-sharing.fix")
| encode-json(prettyPrinting="false")
| json-to-elasticsearch-bulk(idKey="id",type="oerbw",index="oersi")
| write(FLUX_DIR + "oerbw.ndjson");
