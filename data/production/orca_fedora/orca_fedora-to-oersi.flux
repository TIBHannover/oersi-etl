// For data API documentation see:
// https://api.paideia.hbz-nrw.de/public/docs/index.html#!/resource/listResources
// https://api.paideia.hbz-nrw.de/public/docs/index.html#!/resource/listLrmiData

service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";

// First API call, result lists all public ressources
"https://api.paideia.hbz-nrw.de/search/public_orca2/_search"
| open-http(accept="application/json")
| as-records
| match(pattern="_(id)", replacement="$1")
| decode-json(recordPath="$.hits.hits")
// Filters out any resource that is not public
| fix(FLUX_DIR +  "filter.fix")
| literal-to-object
// Second API call, result is single OER
| template("https://api.paideia.hbz-nrw.de/resource/${o}/lrmiData") // local test: FLUX_DIR + "resources/${o}.json"
| catch-object-exception
| open-http(accept="application/json") // local test: open-file
| as-records
| decode-json
| fix(FLUX_DIR +  "orca_fedora.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
