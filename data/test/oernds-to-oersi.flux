"https://www.oernds.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/components/.*")
| open-http
| extract-script
| decode-json
| org.metafacture.metamorph.Metafix(fixFile=FLUX_DIR+"/../edu-sharing.fix")
| encode-json(prettyPrinting="false")
| object-tee | {
    write(FLUX_DIR + "metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://localhost:8080/api/metadata", user="test", pass="test")
};
