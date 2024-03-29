service_domain = "bildungsportal.sachsen.de";
service_id = "https://bildungsportal.sachsen.de";
service_name = "Bildungsportal Sachsen OPAL";

"https://bildungsportal.sachsen.de/opal/oer/content.json"
| open-http(header=user_agent_header, accept="application/json")
| as-records
| decode-json
| filter-null-values
| fix("copy_field('learning_resources[].*','files[].$append')")
| encode-json
| decode-json(recordPath="$.files")
| fix(FLUX_DIR + "opalSachsen.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
