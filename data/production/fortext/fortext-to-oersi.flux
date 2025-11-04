// Get from OAI-PMH, write to local file for testing:
service_domain = "journal.fortext.org/issues/";
service_id = "https://journal.fortext.org/issues/";
service_name = "ForText";
XML_FILE = FLUX_DIR + "forText-metafacture.xml";

"https://journal.fortext.org/api/oai/"
| open-oaipmh(metadataPrefix="oai_dc")
| as-lines
| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix("copy_field('metadata.oai_dc:dc.dc:fullTextUrl.value','detail_url') retain('detail_url')" )
| literal-to-object
| open-http(accept="application/json")
| as-records
| match(pattern="(name=\"DC)\\.(\\w*?)\\.(\\w*?)(\")", replacement="$1_$2_$3$4")
| match(pattern="(name=\"DC)\\.(\\w*?)(\")", replacement="$1_$2$3")
| read-string
| decode-html
| fix(FLUX_DIR + "fortext.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;