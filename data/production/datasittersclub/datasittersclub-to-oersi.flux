service_domain = "https://egov-campus.org/";
service_id = "https://oerworldmap.org/resource/urn:uuid:13dd3971-2768-4de8-bdd8-55f18ed3d752";
service_name = "eGov-Campus";

"https://raw.githubusercontent.com/datasittersclub/site/master/site/_toc.yml"
| open-http(accept="application/json")
| as-records
| decode-yaml
| fix(FLUX_DIR + "datasittersclub_index.fix", *)
| literal-to-object
| oersi.ErrorCatcher(file_errors) 
| open-http
| as-records
| write(FLUX_DIR + "test.json")
;