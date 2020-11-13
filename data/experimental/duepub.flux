"https://duepublico2.uni-due.de/servlets/OAIDataProvider"
| open-oaipmh(metadataPrefix="mods",dateFrom="2020-04-22",dateUntil="2020-04-22")
| decode-xml
| handle-generic-xml
| org.metafacture.metamorph.Metafix("
map(metadata.mods.titleInfo.title.value,title)
map(metadata.mods.abstract.value,description)
map(metadata.mods.accessCondition.href,license)
map(header.identifier.value,url)")
| encode-json(prettyPrinting="false")
| json-to-elasticsearch-bulk(idKey="url",type="duepub",index="oersi")
| write(FLUX_DIR + "duepub.ndjson");
