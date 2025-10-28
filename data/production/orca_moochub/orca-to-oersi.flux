service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";

input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://www.orca.nrw/moochub"
| open-http(header=user_agent_header)
| as-records
| decode-json(recordPath="$.data")
| fix(FLUX_DIR + "orca_moocHub.fix",*)
| encode-json(prettyPrinting="false")
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;