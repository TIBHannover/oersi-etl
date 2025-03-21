service_id = "https://oerworldmap.org/resource/urn:uuid:4a889a12-e481-4319-b145-0782fe51def2";
service_name = "OpenRub";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"PROVIDER SITEMAP"
// Set options for sitemap, url pattern is a filter that only keeps the matching urls.
| oersi.SitemapReader(header=user_agent_header, wait=input_wait, limit=input_limit, urlPattern="(?!.*/en).*/(lernangebot)/.*")
| open-http(header=user_agent_header)
// The following part can change depending where the metadata is provided.
| extract-element("script[type=application/ld+json]")
| match(pattern="@(type|id)", replacement="$1")
| decode-json(recordPath="$.@graph")
// The rest is usually the same.
| fix(FLUX_DIR + "openRub.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
