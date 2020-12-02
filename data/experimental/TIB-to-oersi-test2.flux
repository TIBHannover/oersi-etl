"https://av.tib.eu/sitemap.xml"  // FLUX_DIR + "digiLL-sitemap.xml"
| oersi.SitemapReader(wait="10",limit="20",urlPattern=".*/media/.*")
| open-http(accept="application/ld+json")
| as-lines
| decode-json
| fix(FLUX_DIR + "TIB.fix")
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "TIB-metadata-test2.json", header="[\n", footer="\n]", separator=",\n")
  };