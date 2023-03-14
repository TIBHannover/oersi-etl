service_domain = "https://egov-campus.org/";
service_id = "https://oerworldmap.org/resource/urn:uuid:13dd3971-2768-4de8-bdd8-55f18ed3d752";
service_name = "eGov-Campus";

"https://learn.egov-campus.org/bridges/moochub/courses"
| open-http(accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| filter-null-values
// eGov Campus is partner of MoocHub, we use their moocHub metadata.
| fix(FLUX_DIR + "eGov_moocHub.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, output_schema_resolution_scope, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;