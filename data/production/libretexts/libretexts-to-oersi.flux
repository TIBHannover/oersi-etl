service_domain = "https://commons.libretexts.org";
service_id = "https://commons.libretexts.org";
service_name = "libretexts";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://commons.libretexts.org/api/v1/commons/kbexport"
| open-http(accept="application/json")
| as-records
| decode-json(recordPath="$.titles")
| fix(FLUX_DIR + "libretexts.fix",*)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
