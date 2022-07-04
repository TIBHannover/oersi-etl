service_domain = "oer.uni-kiel.de";
service_id = "https://oer.uni-kiel.de/";
service_name = "fOERde";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://oer.uni-kiel.de/edu-sharing/eduservlet/sitemap?from=" + input_from
| oersi.SitemapReader(wait=input_wait,limit="1000",urlPattern=".*/components/.*",findAndReplace="https://oer.uni-kiel.de/edu-sharing/components/render/(.*)`https://oer.uni-kiel.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| as-lines
| decode-json
// edu-sharing version 6.0
| fix(FLUX_DIR + "foerde_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
