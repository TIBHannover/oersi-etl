service_domain = "https://www.https://futurelearnlab.de/hub/";
service_id = "https://oerworldmap.org/resource/urn:uuid:4acc8053-05a4-4890-9e99-81af4a831933";
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