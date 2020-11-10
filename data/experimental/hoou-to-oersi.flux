"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/materials/.*")
| open-http
| extract-script
| decode-json
| org.metafacture.metamorph.Metafix("
map(_else)")
| encode-json(prettyPrinting="false")
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "hoou-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata", 
      user="test", pass="test", log=FLUX_DIR + "hoou-responses.json")
};