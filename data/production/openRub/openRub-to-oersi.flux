service_id = "https://oerworldmap.org/resource/urn:uuid:4a889a12-e481-4319-b145-0782fe51def2";
service_name = "OpenRub";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://open.ruhr-uni-bochum.de/sitemap.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern="(?!.*/en).*/(lernangebot)/.*")
| open-http
| extract-element("script[type=application/ld+json]")
| match(pattern="@(type|id)", replacement="$1")
| decode-json(recordPath="$.@graph")
| fix(FLUX_DIR + "openRub.fix", *)
| encode-json(prettyPrinting="true")
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
