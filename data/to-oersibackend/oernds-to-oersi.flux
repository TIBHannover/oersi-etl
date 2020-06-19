"https://www.oernds.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/components/.*")
| open-http
| extract-script
| decode-json
| org.metafacture.metamorph.Metafix("

/* Set up the context, TODO: include from separate file */
add_field('@context.id','@id')
add_field('@context.type','@type')
add_field('@context.@vocab','http://schema.org/')

/* Map/pick standard edu-sharing fields, TODO: include from separate file */
map(name, name)
map(description, description)
map(url, id)
map(license, license)
map(thumbnailUrl, thumbnailUrl)

")
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "oernds-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata",
      user="test", pass="test", log=FLUX_DIR + "oernds-responses.json")
};
