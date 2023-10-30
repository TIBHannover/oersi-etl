// Define variables

service_domain = "https://imoox.at/";
service_id = "https://oerworldmap.org/resource/urn:uuid:0332a3bc-6df8-452d-8c58-2721ad816192";
service_name = "iMoox";

"PROVIDER BULK URL"
| open-http(header=user_agent_header, accept="application/json")
| as-lines
// define where records reside
| decode-json(recordPath="$.data")
// The rest is usually the same.
| fix(FLUX_DIR + "generalTemplate.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;