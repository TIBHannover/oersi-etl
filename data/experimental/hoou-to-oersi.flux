"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/materials/.*")
| open-http
| decode-html
| org.metafacture.metamorph.Metafix("

/* Set up the context, TODO: include from separate file */
add_field('@context.id','@id')
add_field('@context.type','@type')
add_field('@context.@vocab','http://schema.org/')

/* Map some of the data we have to the oersi model: */
map(html.head.title.value, name)
map(html.body.div.div.div.div.div.div.div.p.value, description)
map(html.body.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.p.a.href, license)

/* TODO: pick out correct html.head.meta.content or html.head.script.value for URL*/
map(html.body.div.div.div.div.div.div.div.div.div.div.p.a.href, id)

")
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "hoou-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata", 
      user="test", pass="test", log=FLUX_DIR + "hoou-responses.json")
};