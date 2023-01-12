// For data API documentation see:
// https://api.paideia.hbz-nrw.de/public/docs/index.html#!/resource/listResources
// https://api.paideia.hbz-nrw.de/public/docs/index.html#!/resource/listLrmiData

service_domain = "https://www.orca.nrw";
service_id = "https://www.orca.nrw";
service_name = "ORCA.nrw (hoerkaen)";

// First API call, result lists all public ressources
"https://api.hoerkaen.hbz-nrw.de/search/public_orca2/_search?size=10000"
| open-http(accept="application/json")
| as-records
| match(pattern="_(id)", replacement="$1")
| decode-json(recordPath="$.hits.hits")
// Filters out any resource that is not public
| fix(FLUX_DIR +  "filter.fix")
| literal-to-object
// Second API call, result is single OER
| template("https://api.hoerkaen.hbz-nrw.de/resource/${o}/lrmiData") // local test: FLUX_DIR + "resources/${o}.json"
| oersi.ErrorCatcher(file_errors)
| open-http(accept="application/json") // local test: open-file
| oersi.ErrorCatcher(file_errors)
| as-records
| decode-json
| fix(FLUX_DIR +  "orca_toScience.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
