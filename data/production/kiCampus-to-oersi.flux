service_domain = "https://learn.ki-campus.org";
service_id = "https://oerworldmap.org/resource/urn:!!!!______NEEDS_ENTRY_TOO___!!!!";
service_name = "KI Campus";

"https://learn.ki-campus.org/bridges/moochub/courses"
| open-http(accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| filter-null-values
// KI Campus is partner of MoocHub, we use their moocHub metadata.
| fix(FLUX_DIR + "kiCampus_moocHub.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;