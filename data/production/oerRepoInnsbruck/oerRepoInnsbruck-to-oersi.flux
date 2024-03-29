service_domain="oer-repo.uibk.ac.at";
service_id="https://oer-repo.uibk.ac.at/edu-sharing/";
service_name="Universität Innsbruck OER Repositorium";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://oer-repo.uibk.ac.at/edu-sharing/rest/search/v1/queries/-home-/-default-/ngsearch?maxItems=10&skipCount=0&propertyFilter=-all-"
| oersi.JsonApiReader(header=user_agent_header, method="post", body="{\"resolveCollections\": false, \"criteria\": [], \"facets\": []}", recordPath="nodes", pageParam="skipCount", stepSize="10", totalLimit=input_limit)
| decode-json
| filter-null-values
// edu-sharing version 8.1
| fix(FLUX_DIR + "oerRepoInnsbruck_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
