"https://www.oernds.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| oersi.SitemapReader(wait="1000",limit="2",urlPattern=".*/components/.*")
| open-http
| extract-script
| decode-json
| org.metafacture.metamorph.Metafix("
/* Map some of the data we have to the oersi model: */
map(name,         educationalResource.name)
map(description,  educationalResource.description)
map(url,          educationalResource.url)
map(identifier,   educationalResource.identifier)
map(license,      educationalResource.license)
map(thumbnailUrl, educationalResource.thumbnailUrl)

/* We have some 'unknown' inLanguge, hard code for now: */
add_field(        educationalResource.inLanguage, 'de')

/* Add some empty fields required in the oersi model: */
add_field(        educationalResource.subject,'')
add_field(        educationalResource.learningResourceType,'')

/* Add constant data for this specific workflow: (TODO: extract other to edu-sharing.fix) */
add_field(source, 'https://www.oernds.de')
")
| encode-json
| oersi.FieldMerger
| object-tee | {
    write(FLUX_DIR + "metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata", user="test", pass="test")
};
