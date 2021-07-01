service_domain = "oer-contentbuffet.info";
service_id = "https://oerworldmap.org/resource/urn:uuid:efed6ca2-b228-480f-be03-090a19de7b42";
service_name = "OERinfo";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

// Query should only ask for collection OER but DuePublico does not offer this via its OAI: // 
"https://duepublico2.uni-due.de/oer/oai"
| open-oaipmh(metadataPrefix="mods",dateFrom="2021-01-01",dateUntil="2021-07-01")
| decode-xml
| handle-generic-xml
| fix(FLUX_DIR + "mods.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
//| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
| print
;
