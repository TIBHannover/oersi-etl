service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";

"https://dist.orca-staging.educast.cloud/oaipmh/default" //Prod-System: https://dist.orca.educast.cloud/oaipmh/default
| open-oaipmh(metadataPrefix="matterhorn-inlined")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix-filter("
do map('metadata.inlined.catalog.metadata.terms:oersi-unlist.value')
	equals(string: 'false')
end
") // filter out all deleted records
| fix(FLUX_DIR + "orca_educast.fix", *)
| encode-json(prettyPrinting="true")
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
