"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait="10",limit="2",urlPattern=".*/materials/.*")
| open-http
| extract-element ("script[data-test='model-linked-data']") 
| decode-json
| fix(FLUX_DIR + "hoou.fix", *) 
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "hoou-metadata.json", header="[\n", footer="\n]", separator=",\n")
  };