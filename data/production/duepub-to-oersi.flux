service_domain = "https://duepublico2.uni-due.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:c9d74da8-bf88-4f3e-b601-f2a3f6d40330";
service_name = "DuEPublico";

"https://duepublico2.uni-due.de/oer/oai"
| open-oaipmh(metadataPrefix="mods")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix-filter("
map('metadata.mods:mods.mods:identifier.value')
") // filter out all deleted records
// due to the complex structure of the MODs a morph instead of a fix is used.
| morph(FLUX_DIR + "duepub-morph.xml", *) // '*': pass all flux variables to the fix
| encode-json
// The field merger in this case is not needed, since process control allows to deduplicate and separate.
//| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
