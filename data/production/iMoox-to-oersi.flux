service_domain = "https://imoox.at/";
service_id = "https://oerworldmap.org/resource/urn:uuid:0332a3bc-6df8-452d-8c58-2721ad816192";
service_name = "iMoox";

"https://imoox.at/mooc/local/moochubs/classes/webservice.php"
| open-http(accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| fix(FLUX_DIR + "moochub.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;