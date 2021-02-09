service_domain = "www.oerbw.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:4062c64d-b0ac-4941-95c2-8116f137326d";
service_name = "ZOERR";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://www.oerbw.de/edu-sharing/eduservlet/sitemap?from=" + input_from
| oersi.SitemapReader(wait=input_wait,limit=input_limit,urlPattern=".*/components/.*",findAndReplace="https://uni-tuebingen.oerbw.de/edu-sharing/components/render/(.*)`https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| as-lines
| decode-json
| fix(FLUX_DIR + "edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid = FLUX_DIR + "zoerr-metadata.json", writeInvalid = FLUX_DIR + "zoerr-invalid.json")
| oersi.OersiWriter(backend_api,user=backend_user, pass=backend_pass, log = FLUX_DIR + "zoerr-responses.json")
;
