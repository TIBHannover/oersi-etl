service_id = "https://openmusic.academy";
service_name = "Open Music Academy";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";
default open_music_academy_api_key = "x-api-key: 9483A6E3ED5D4475AABD82D306549FE0";
default open_music_academy_api_url = "https://staging.openmusic.academy/api/v1/amb/metadata";

open_music_academy_api_url
| open-http(accept="application/json", header=open_music_academy_api_key)
| as-lines
| decode-json(recordPath="$")
| fix(FLUX_DIR + "openMusicAcademy.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, output_schema_resolution_scope, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
