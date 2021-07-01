"https://duepublico2.uni-due.de/servlets/OAIDataProvider"
| open-oaipmh(metadataPrefix="mods",dateFrom="2020-05-14",dateUntil="2020-05-14")
| decode-xml
| handle-generic-xml
| org.metafacture.metamorph.Metafix("

/* Set up the context, TODO: include from separate file */
add_field('@context.id','@id')
add_field('@context.type','@type')
add_field('@context.@vocab','http://schema.org/')

/* Map some of the data we have to the oersi model: */
map(metadata.mods.titleInfo.title.value, name)
map(metadata.mods.abstract.value, description)
/* TODO: pick a specific URL: */
map(metadata.mods.location.url.value, id)

/* Dont use the actual license, API accepts only creativecommons.org URLs */
/* map(metadata.mods.accessCondition.href, license) */
/* TODO: change constraint or use mapping of duepublico to creativecommons URLs */
add_field(license, 'https://creativecommons.org/licenses/unknown')

")
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "duepub-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata", 
      user="test", pass="test", log=FLUX_DIR + "duepub-responses.json")
};