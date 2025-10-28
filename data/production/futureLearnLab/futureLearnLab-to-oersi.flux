service_domain = "https://futurelearnlab.de/hub/";
service_id = "https://futurelearnlab.de/hub/";
service_name = "Future Learn Lab Lübeck";

input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://futurelearnlab.de/hub/local/ildmeta/get_moochub_courses.php"
| open-http(header=user_agent_header)
| as-records
| decode-json(recordPath="$.data")
| fix(FLUX_DIR + "futureLearnLab_moocHub.fix",*)
| encode-json(prettyPrinting="false")
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
