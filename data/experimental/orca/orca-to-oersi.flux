// For data API documentation see:
// https://api.hoerkaen.hbz-nrw.de/public/docs/index.html#!/resource/listResources
// https://api.hoerkaen.hbz-nrw.de/public/docs/index.html#!/resource/listLrmiData

orca_auth="changeme"

// local test: FLUX_DIR + "resources/list.json"
"https://api.hoerkaen.hbz-nrw.de/resource?contentType=oer"
| open-http(auth=orca_auth, accept="application/json") // local test: open-file
| as-records
| decode-json(recordPath="$")
| fix("map('@id','id')")
| literal-to-object
| template("https://api.hoerkaen.hbz-nrw.de/resource/${o}/lrmiData") // local test: FLUX_DIR + "resources/${o}.json"
| catch-object-exception
| open-http(auth=orca_auth, accept="application/json") // local test: open-file
| as-records
| oersi.JsonValidator("resource:/schemas/schema.json", writeValid=metadata_valid, writeInvalid=metadata_invalid)
| print
;