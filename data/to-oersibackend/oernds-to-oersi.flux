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
map(url, mainEntityOfPage.id)
map(thumbnailUrl, image)

do combine('@fullCreator', '${last}, ${first}')
  map(creator.givenName,first)
  map(creator.familyName,last)
end

map('@fullCreator','creator[]..name')
map(creator.legalName,'creator[]..name')
map('creator.@type','creator[]..type')

replace_all(license, '/deed.*$', '')
replace_all(inLanguage, 'unknown', 'de')

lookup(learningResourceType,
/* TODO: support lookup in CSV file */
Kurs: 'https://w3id.org/kim/hcrt/course',
course: 'https://w3id.org/kim/hcrt/course',
image: 'https://w3id.org/kim/hcrt/image',
video: 'https://w3id.org/kim/hcrt/video',
reference: 'https://w3id.org/kim/hcrt/index',
presentation: 'https://w3id.org/kim/hcrt/slide',
schoolbook: 'https://w3id.org/kim/hcrt/text',
script: 'https://w3id.org/kim/hcrt/script',
worksheet: 'https://w3id.org/kim/hcrt/worksheet',
__default: 'https://w3id.org/kim/hcrt/other')

map('@learningResourceType', learningResourceType.id)
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
