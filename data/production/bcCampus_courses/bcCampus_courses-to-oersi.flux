service_domain = "https://bccampus.ca/";
service_id = "https://bccampus.ca/";
service_name = "BC Campus";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://api.collection.bccampus.ca/api/sitemap/index.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern=".*/(course-packs)/.*")
| oersi.ErrorCatcher(file_errors)
| open-http(header=user_agent_header)
| extract-element("script[type=application/ld+json]")
| match(pattern="@(type|id)", replacement="$1")
| decode-json
| fix(FLUX_DIR + "bcCampus_courses.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;