

// For data API documentation see:
// https://api.hoerkaen.hbz-nrw.de/public/docs/index.html#!/resource/listResources
// https://api.hoerkaen.hbz-nrw.de/public/docs/index.html#!/resource/listLrmiData

service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";
orca_auth="changeme";

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
| decode-json
| fix(FLUX_DIR +  "orca-Fedora.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("resource:/schemas/schema.json", writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
