service_domain="www.twillo.de";
service_id="https://oerworldmap.org/resource/urn:uuid:51277cb8-c5a4-4204-aeaf-10ee06df53ce";
service_name="twillo";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://www.twillo.de/edu-sharing/eduservlet/sitemap?from=" + input_from
| oersi.SitemapReader(wait=input_wait,limit=input_limit,urlPattern=".*/components/.*",findAndReplace="https://www.twillo.de/edu-sharing/components/render/(.*)`https://www.twillo.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| as-lines
| decode-json
// edu-sharing version 6.0
| fix(FLUX_DIR + "twillo_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
