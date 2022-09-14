service_domain = "https://open.umn.edu/opentextbooks/";
service_id = "https://open.umn.edu/opentextbooks/";
service_name = "Open Textbook Library";


// Data is provided as MARC21-Dum.
"https://open.umn.edu/opentextbooks/download.mrc"
| open-http
| as-records
| decode-marc21
| fix(FLUX_DIR + "openTextbookLibrary.fix",*)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
