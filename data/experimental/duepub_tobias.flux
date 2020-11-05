// Query should only ask for collection OER but DuePublico does not offer this via its OAI: // 
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

/* Mapping the data to element level: */
map(metadata.mods.identifier.value, id)

/* To map the image the location.url needs to carry the attribute access preview. Could creat confusion:*/
map(metadata.mods.location.url.value, image)


map(metadata.mods.titleInfo.title.value, name)

/* name does not specify if creator type is person or institution: */
/*  map(metadata.mods.name.displayForm.value, creator) */
do combine('name', '${first} ${last}')
    map(node.createdBy.firstName,first)
    map(node.createdBy.lastName,last)
end

/* Do we additionally need the language of the abstract?: */
map(metadata.mods.abstract.value, description)

/* Map has to be updated, when Duepublico2 Updates their links  */
do map(metadata.mods.accessCondition.href, license)
  lookup(in: 'data/maps/DuePub-Licence.tsv')
end

map(metadata.mods.originInfo.dateIssued.value, dataCreated)
map(metadata.mods.relatedItem.language.languageTerm.value, inLanguage)
map(metadata.mods.genre.value, learningResourceType) 

/* Maps only a single keyword */
map(metadata.mods.subject.topic.value, keywords) 

/* map(_else) */

/* Fehlen noch: */
/* map(metadata.mods. , @context) */
/* map(metadata.mods.typeOfResource.value, type) vocab does not match */
/* map(metadata.mods. , about) */
/* map(metadata.mods. , publisher) Who is the publisher? The Resource or only the real publisher? */
/* map(metadata.mods. , audience) DuePublico is missing audience */
/* map(metadata.mods. , isBasedOn) */
/* map(metadata.mods. , mainEntityOfPage) */


")
| encode-json
| oersi.FieldMerger
// | oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
|  write(FLUX_DIR + "duepub-metadata-tobias-neu.json", header="[\n", footer="\n]", separator=",\n");