service_domain = "https://duepublico2.uni-due.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:c9d74da8-bf88-4f3e-b601-f2a3f6d40330";
service_name = "DuEPublico";

infile = FLUX_DIR + "var.json";

infile
| open-file
| as-records
| decode-json
| fix(FLUX_DIR + "var.fix", *)
| encode-json(prettyPrinting="true")
| print
;