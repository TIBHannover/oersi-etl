"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| open-http                       // open-file
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/materials/.*")
| open-http
| decode-html
| org.metafacture.metamorph.Metafix("
/* Map some of the data we have to the oersi model: */
map(html.head.title.value, educationalResource.name)
map(html.body.div.div.div.div.div.div.div.p.value, educationalResource.description)
map(html.body.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.div.p.a.href, educationalResource.license)

/* TODO: pick out correct html.head.meta.content or html.head.script.value for URL*/
map(html.body.div.div.div.div.div.div.div.div.div.div.p.a.href, educationalResource.url)

/* Add some fields required in the oersi model: */
add_field(educationalResource.inLanguage, 'de')
add_field(educationalResource.subject,'')
add_field(educationalResource.learningResourceType,'')

/* Add constant data for this specific workflow: */
add_field(source,'https://www.hoou.de')
")
| encode-json
| oersi.FieldMerger
| object-tee | {
    write(FLUX_DIR + "hoou-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata", 
      user="test", pass="test", log=FLUX_DIR + "hoou-responses.json")
};