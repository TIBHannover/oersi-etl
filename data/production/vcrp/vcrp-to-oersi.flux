service_domain = "https://openedu-rlp.de";
service_id = "https://openedu-rlp.de/edu-sharing/";
service_name = "VCRP";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://openedu-rlp.de/edu-sharing/rest/search/v1/queriesV2/-home-/-default-/ngsearch?maxItems=10&skipCount=0&propertyFilter=-all-"
| oersi.JsonApiReader(method="post", body="{\"criterias\": [], \"facettes\": []}", recordPath="nodes", pageParam="skipCount=", stepSize="10", totalLimit=input_limit)
| decode-json
| filter-null-values
// edu-sharing version 6.0
| fix(FLUX_DIR + "vcrp_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;