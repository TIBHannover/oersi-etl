service_id = "https://openmusic.academy";
service_name = "Open Music Academy";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"http://oersi01.develop.service.tib.eu:12380/oma"  // TIB-internal proxy-workaround as long as metafacture is not able to set custom-headers - see https://github.com/metafacture/metafacture-core/issues/456 (original API-url https://openmusic.academy/api/v1/amb/metadata)
| open-http(accept="application/json")
| as-lines
| decode-json(recordPath="$")
| fix(FLUX_DIR + "openMusicAcademy.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
