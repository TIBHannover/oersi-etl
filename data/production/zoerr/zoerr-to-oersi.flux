service_domain = "www.oerbw.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:4062c64d-b0ac-4941-95c2-8116f137326d";
service_name = "ZOERR";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://www.oerbw.de/edu-sharing/eduservlet/sitemap?from=" + input_from
| oersi.SitemapReader(wait=input_wait,limit=input_limit,urlPattern=".*/components/.*",findAndReplace="https://www.zoerr.de/edu-sharing/components/render/(.*)`https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| oersi.ErrorCatcher(file_errors)
| as-lines
| decode-json
// edu-sharing version 6.0
| fix(FLUX_DIR + "zoerr_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
