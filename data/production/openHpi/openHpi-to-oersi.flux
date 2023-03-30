service_domain = "https://open.hpi.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:75c7c768-c5b2-47ad-bb62-08fb1db2d74b";
service_name = "openHPI";

"https://open.hpi.de/bridges/moochub/courses"
| open-http(header=user_agent_header, accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| filter-null-values
// openHPI is partner of MoocHub, we use their moocHub metadata.
| fix(FLUX_DIR + "openHpi_moocHub.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;