service_domain = "https://media.hhu.de/";
service_id = "https://media.hhu.de/";
service_name = "HHU Mediathek";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://media.hhu.de/sitemap/video" // FLUX_DIR + "hhu-sitemap.xml"
| oersi.SitemapReader(header=user_agent_header, wait=input_wait, limit=input_limit, tag="player_loc")
| open-http(header=user_agent_header)
| decode-html(attrValsAsSubfields="&p.class&a.class&div.class&span.class")
// useful for debugging and seeing full flattened input field names:
//| list-fix-paths
| fix(FLUX_DIR + "hhu.fix", *)
| encode-json(prettyPrinting="false")
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
//| print
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;