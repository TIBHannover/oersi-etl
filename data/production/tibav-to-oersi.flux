service_domain = "https://av.tib.eu/";
service_id = "https://oerworldmap.org/resource/urn:uuid:10c5092a-152d-4cc9-a823-e2deff43128e";
service_name = "TIB AV-Portal";

"https://www.tib.eu/oai/public/repository/av-portal"
| open-oaipmh(metadataPrefix="rdf_jsonld")
| decode-xml
| handle-generic-xml
| fix("map(metadata.value)", *)
| literal-to-object
| decode-json
| fix(FLUX_DIR + "tibav.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;