service_domain = "https://www.hoou.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:ac5d5269-6449-43c0-b43b-2ed372763a0e";
service_name = "HOOU";

default input_limit = "5"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://open.ruhr-uni-bochum.de/sitemap.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern="(?!.*/en).*/(lernangebot)/.*")
| open-http
| extract-element("script[type=application/ld+json]")
| match(pattern="@(type|id)", replacement="$1")
| decode-json(recordPath="$.@graph")
| metafix(FLUX_DIR + "openRub.fix", *)
| encode-json(prettyPrinting="true")
| oersi.JsonValidator("resource:/schemas/schema.json", writeValid=metadata_valid, writeInvalid=metadata_invalid)
| write(FLUX_DIR + "openRub.json")
;
