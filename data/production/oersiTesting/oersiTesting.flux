service_id = "https://gitlab.com/oersi/oersi-test-files/";
service_name = "OERSI Testing";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://gitlab.com/oersi/oersi-test-files/-/raw/main/testresource.txt" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| open-http
| as-lines
| open-http
| as-records
| decode-json
// Add or create mainEntityOfPage
| fix(FLUX_DIR + "oersiTesting.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
