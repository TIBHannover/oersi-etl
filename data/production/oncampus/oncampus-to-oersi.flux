service_domain = "https://www.oncampus.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:4acc8053-05a4-4890-9e99-81af4a831933";
service_name = "oncampus";

input_limit = "10"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://www.oncampus.de/sitemap.xml" // FLUX_DIR + "oncampus-sitemap.xml"
| oersi.SitemapReader(wait=input_wait,limit=input_limit)
| open-http
| decode-html(attrValsAsSubfields="&h3.class&h4.class")
//| fix("nothing()",repeatedFieldsToEntities="true")
//| flatten
| fix(FLUX_DIR + "oncampus.fix", *, repeatedFieldsToEntities="true")
| encode-json(prettyPrinting="true")
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| print
//| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;