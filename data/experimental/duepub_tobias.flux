"https://duepublico2.uni-due.de/servlets/OAIDataProvider"
| open-oaipmh(metadataPrefix="mods",dateFrom="2020-05-14",dateUntil="2020-05-14")
| decode-xml
| handle-generic-xml
| org.metafacture.metamorph.Metafix("

/* Set up the context */
do array('@context')
 add_field('','https://w3id.org/kim/lrmi-profile/draft/context.jsonld')
 do entity('')
  add_field('@language', 'de')
 end
end

/* Mapping the data to element level */
map(metadata.mods.identifier.value, id)
map(metadata.mods.location.url.value, image) 
/* hier scheint das Attribut Access preview noch benötigt werden */
map(metadata.mods.titleInfo.title.value, name)
map(metadata.mods.name.displayForm.value, creator)
/* bisher gibt es keine Mods Auslese, ob Person oder Institution, wenn ich das richtig lese */
map(metadata.mods.abstract.value, description)
/* Sprache relevant? metadata.mods.abstract.lang */
map(metadata.mods.accessCondition.href, license) 
/* hier muss noch nachträglich gemappt werden */
map(metadata.mods.originInfo.dateIssued.value, dataCreated)
map(metadata.mods.relatedItem.language.languageTerm.value, inLanguage)
map(metadata.mods.genre.value, learningResourceType) 
/* hier eventuell doch type? */
map(metadata.mods.subject.topic.value, keywords) 
/* hier wird in der XML nur ein Wert ausgelesen, obwohl es mehrere gibt */
/* map(_else) */

/* Fehlen noch: */
/* map(metadata.mods. , @context) */
/* map(metadata.mods.typeOfResource.value, type) das passt nicht zur Werteliste */
/* map(metadata.mods. , about) */
/* map(metadata.mods. , publisher) Muss hier standardmäßig Duepublico 2 oder ein realer Verlag hin? */
/* map(metadata.mods. , audience) gibts bei duepublico2 nicht */
/* map(metadata.mods. , isBasedOn) */
/* map(metadata.mods. , mainEntityOfPage) */


")
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
|  write(FLUX_DIR + "duepub-metadata-tobias-neu.json", header="[\n", footer="\n]", separator=",\n");