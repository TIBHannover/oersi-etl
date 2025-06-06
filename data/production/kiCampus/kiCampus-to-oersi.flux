service_domain = "https://learn.ki-campus.org";
service_id = "https://oerworldmap.org/resource/urn:uuid:b092c55e-038c-4634-8063-77f9aa7b7404";
service_name = "KI Campus";

"https://moodle.ki-campus.org/local/open_api/courses.php"
| open-http(header=user_agent_header, accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| filter-null-values
// KI Campus is partner of MoocHub, we use their moocHub metadata.
| fix(FLUX_DIR + "kiCampus_moocHub.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;