service_id = "https://statable.eu/";
service_name = "Statable";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://statable.eu/sitemap.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(header=user_agent_header, wait=input_wait, limit=input_limit)
| open-http(header=user_agent_header)
| extract-element("script[type=application/ld+json]")
| match(pattern="@(type|id)", replacement="$1")
| decode-json(recordPath="$")
| fix(FLUX_DIR + "statable.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
