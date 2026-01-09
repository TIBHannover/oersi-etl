service_domain = "www.oerbw.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:4062c64d-b0ac-4941-95c2-8116f137326d";
service_name = "ZOERR";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://www.zoerr.de/edu-sharing/rest/search/v1/queries/-home-/-default-/ngsearch?maxItems=10&skipCount=0&propertyFilter=-all-"
| oersi.JsonApiReader(header=user_agent_header, method="post", body="{\"resolveCollections\": false, \"criteria\": [], \"facets\": []}", recordPath="nodes", pageParam="skipCount", stepSize="10", totalLimit=input_limit)
| decode-json
| filter-null-values
// edu-sharing version 8.0
| fix(FLUX_DIR + "zoerr_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
