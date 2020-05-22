"https://duepublico2.uni-due.de/servlets/OAIDataProvider"
| open-oaipmh(metadataPrefix="mods",dateFrom="2020-05-14",dateUntil="2020-05-14")
| decode-xml
| handle-generic-xml
| org.metafacture.metamorph.Metafix("
/* Map some of the data we have to the oersi model: */
map(metadata.mods.titleInfo.title.value, educationalResource.name)
map(metadata.mods.abstract.value,        educationalResource.description)
map(header.identifier.value,             educationalResource.identifier)

/* TODO: pick a specific URL */
map(metadata.mods.location.url.value,    educationalResource.url)

/* Dont use the actual license, API accepts only creativecommons.org URLs */
/* map(metadata.mods.accessCondition.href,educationalResource.license) */
/* TODO: change constraint or use mapping of duepublico to creativecommons URLs */
add_field(educationalResource.license, 'https://creativecommons.org/licenses/unknown')

/* Add some fields required in the oersi model: */
add_field(educationalResource.inLanguage, 'de')
add_field(educationalResource.subject,'')
add_field(educationalResource.learningResourceType,'')

/* Add constant data for this specific workflow: */
add_field(source, 'https://duepublico2.uni-due.de')
")
| encode-json
| oersi.FieldMerger
| object-tee | {
    write(FLUX_DIR + "duepub-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter("http://192.168.98.115:8080/oersi/api/metadata", 
      user="test", pass="test", log=FLUX_DIR + "duepub-responses.json")
};