service_domain = "https://open.vhb.org/";
service_id = "https://oerworldmap.org/resource/urn:uuid:46c8a3e5-1d91-4f07-b29e-b4f254aa6010";
service_name = "Open VHB";

"https://open.vhb.org/moochub_new.json"
| open-http(accept="application/json")
| as-lines
| decode-json(recordPath="$.data")
| filter-null-values
| fix(FLUX_DIR + "openVhb_moocHub.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;