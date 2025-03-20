service_domain = "https://mediathek.hhu.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:b6c2027f-b747-4c66-9cd8-1e363659af9b";
service_name = "HHU Mediathek";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://mediathek.hhu.de/sitemap?list=videos" // FLUX_DIR + "hhu-sitemap.xml"
| oersi.SitemapReader(header=user_agent_header, wait=input_wait, limit=input_limit)
| open-http(header=user_agent_header)
| decode-html(attrValsAsSubfields="&p.class&a.class&div.class&span.class")
// useful for debugging and seeing full flattened input field names:
//| fix("nothing()",repeatedFieldsToEntities="true") | flatten
| fix(FLUX_DIR + "hhu.fix", *)
| encode-json(prettyPrinting="false")
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
//| print
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;