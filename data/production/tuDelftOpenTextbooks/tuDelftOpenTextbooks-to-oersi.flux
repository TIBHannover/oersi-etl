service_domain = "textbooks.open.tudelft.nl";
service_id = "https://textbooks.open.tudelft.nl";
service_name = "TU Delft Open Textbooks";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";


"https://books.open.tudelft.nl/home/oai" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| open-oaipmh(metadataPrefix="oai_dc")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix("
	do list(path:'metadata.oai_dc:dc.dc:identifier','var':'$i')
		if any_contain('$i.value', 'books.open.tudelft.nl')
			copy_field('$i.value','url')
		end
	end
	retain('url')
	unless exists('url')
		reject()
	end")

| list-fix-values("url", count="false")
| oersi.ErrorCatcher(file_errors)
| open-http(header=user_agent_header)
| as-records
| match(pattern="(name=\"DC)\\.(\\w*?)\\.(\\w*?)(\")", replacement="$1_$2_$3$4")
| match(pattern="(name=\"DC)\\.(\\w*?)(\")", replacement="$1_$2$3")
| read-string
| decode-html(attrValsAsSubfields="&p.class&a.class&div.class&span.class")
| fix(FLUX_DIR + "tuDelftOpenTextbooks.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
