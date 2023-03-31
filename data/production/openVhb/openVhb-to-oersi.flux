service_domain = "https://open.vhb.org/";
service_id = "https://oerworldmap.org/resource/urn:uuid:46c8a3e5-1d91-4f07-b29e-b4f254aa6010";
service_name = "vhb";

// Cumstomized JSON for OERSI based on moocHub JSON-data.
"https://open.vhb.org/oersi.json"
| open-http(header=user_agent_header, accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| filter-null-values
// openVhb is partner of MoocHub, we use their moocHub metadata with added infos for OERSI.
| fix(FLUX_DIR + "openVhb_moocHub.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;