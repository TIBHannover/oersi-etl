// Define variables

service_domain = "https://av.tib.eu/";
service_id = "https://oerworldmap.org/resource/urn:uuid:10c5092a-152d-4cc9-a823-e2deff43128e";
service_name = "TIB AV-Portal";

// Configure OAI-PMH Workflow 
"[PROVIDER OAI PMH]"
// Set needed options for OAIPMH Arguments, different for every workflow
// also see: https://github.com/metafacture/metafacture-documentation/blob/master/flux-commands.md#open-oaipmh
| open-oaipmh(metadataPrefix="e.g. datacite", setSpec="e.g. collection~kmo-av_solr~documentFormat:el")
// There rest usually is the same:
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "generalTemplate.fix", *) //The star hands over the flux variables to the fix.
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.ErrorCatcher(file_errors)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;