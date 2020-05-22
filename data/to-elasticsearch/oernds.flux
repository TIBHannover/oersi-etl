"https://www.oernds.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/components/.*")
| open-http
| extract-script
| decode-json
| org.metafacture.metamorph.Metafix(fixFile=FLUX_DIR+"edu-sharing.fix")
| encode-json(prettyPrinting="false")
| json-to-elasticsearch-bulk(idKey="identifier",type="oernds",index="oersi")
| write(FLUX_DIR + "oernds.ndjson");
