service_domain = "oer.hoou.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:ac5d5269-6449-43c0-b43b-2ed372763a0e";
service_name = "HOOU_edusharing";


default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://oer.hoou.de/edu-sharing/rest/search/v1/queries/-home-/-default-/ngsearch?maxItems=10&skipCount=0&propertyFilter=-all-"
| oersi.JsonApiReader(header=user_agent_header, method="post", body="{\"resolveCollections\": false, \"criteria\": [], \"facets\": []}", recordPath="nodes", pageParam="skipCount", stepSize="10", totalLimit="250")
| decode-json
| filter-null-values
// edu-sharing version 8.0
| fix(FLUX_DIR + "hoou_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
