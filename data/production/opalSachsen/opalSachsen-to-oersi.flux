service_domain = "https://bildungsportal.sachsen.de";
service_id = "https://bildungsportal.sachsen.de";
service_name = "eGov-Campus";

"https://bildungsportal.sachsen.de/opal/oer/content.json"
| open-http(accept="application/json")
| as-records
| decode-json
| filter-null-values
| fix("copy_field('learning_resources[].*','files[].$append')")
| encode-json
| decode-json(recordPath="$.files")
| fix(FLUX_DIR + "opalSachsen.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
