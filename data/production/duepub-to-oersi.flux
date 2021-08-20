service_domain = "duepublico2.uni-due.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:DUMMI";
service_name = "DuePublico 2";

"https://duepublico2.uni-due.de/oer/oai"
| open-oaipmh(metadataPrefix="mods")
| decode-xml
| handle-generic-xml
| fix-filter("
map('metadata.mods.identifier.value')
") // filter out all deleted records
// due to the complex structure of the MODs a morph instead of a fix is used.
| morph(FLUX_DIR + "duepub-morph.xml", *) // '*': pass all flux variables to the fix
| encode-json
// The field merger in this case is not needed, since process control allows to deduplicate and separate.
//| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
