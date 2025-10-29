service_domain = "https://www.hoou.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:ac5d5269-6449-43c0-b43b-2ed372763a0e";
service_name = "HOOU";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://legacy.hoou.de/sitemap.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(header=user_agent_header, wait=input_wait, limit=input_limit, urlPattern=".*/(materials|projects)/.*",findAndReplace="www.hoou.de`legacy.hoou.de")
| oersi.ErrorCatcher(file_errors)
| open-http(header=user_agent_header)
| extract-element("script[data-test=model-linked-data]")
| match(pattern="@(type|id)", replacement="$1")
| decode-json
| filter-null-values
| fix(FLUX_DIR + "hoou.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;