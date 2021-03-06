service_domain = "oer-contentbuffet.info";
service_id = "https://oerworldmap.org/resource/urn:uuid:efed6ca2-b228-480f-be03-090a19de7b42";
service_name = "OERinfo";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://oer-contentbuffet.info/edu-sharing/eduservlet/sitemap?type=io&from=" + input_from
| oersi.SitemapReader(wait=input_wait,limit=input_limit,urlPattern=".*/components/.*",findAndReplace="https://oer-contentbuffet.info/edu-sharing/components/render/(.*)`https://oer-contentbuffet.info/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| as-lines
| decode-json
| fix(FLUX_DIR + "edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;