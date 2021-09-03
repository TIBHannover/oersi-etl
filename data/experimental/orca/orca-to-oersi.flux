// For production data API see:
// https://api.hoerkaen.hbz-nrw.de/public/docs/index.html#!/resource/listResources
// https://api.hoerkaen.hbz-nrw.de/public/docs/index.html#!/resource/listLrmiData

FLUX_DIR + "resources/list.json" // PROD: /resource?contentType=oer
| open-file // PROD: open-http
| as-records
| decode-json(recordPath="$")
| fix("map('@id','id')")
| literal-to-object
| template(FLUX_DIR + "resources/${o}.json") // PROD: e.g. /resource/oer:11/lrmiData
| open-file // PROD: open-http
| as-records
| oersi.JsonValidator("resource:/schemas/schema.json", writeValid=metadata_valid, writeInvalid=metadata_invalid)
| print
;