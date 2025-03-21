service_domain = "https://www.oncampus.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:4acc8053-05a4-4890-9e99-81af4a831933";
service_name = "oncampus";

input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://www.oncampus.de/sitemap.xml" // FLUX_DIR + "oncampus-sitemap.xml"
| oersi.SitemapReader(header=user_agent_header, wait=input_wait,limit=input_limit)
| open-http(header=user_agent_header)
| as-records
| filter-strings("<h3 class=\"productcost\">(free|kostenlos)</h3>")
| read-string
| oersi.HtmlDecoder(attrValsAsSubfields="&h3.class&h4.class&a.class")
// useful for debugging and seeing full flattened input field names:
//| fix("nothing()",repeatedFieldsToEntities="true") | flatten
| fix(FLUX_DIR + "oncampus.fix", *)
| encode-json(prettyPrinting="false")
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
// useful for debugging without running oersi-setup / -backend:
//| print
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;