// Define Variables

service_domain = "https://imoox.at/";
service_id = "https://oerworldmap.org/resource/urn:uuid:0332a3bc-6df8-452d-8c58-2721ad816192";
service_name = "iMoox";

// Configure workflow
"[Provider URL for MoocHub Metadata.]"
| open-http(header=user_agent_header, accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
// ... is partner of MoocHub, we use their moocHub metadata.
| fix(FLUX_DIR + "template_moocHub.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;