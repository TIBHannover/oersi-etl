service_domain = "langsci-press.org/index";
service_id = "https://langsci-press.org/";
service_name = "langSci Press";

"https://raw.githubusercontent.com/langsci/opendata/master/catalog.csv"
| open-http(accept="application/csv")
| as-lines
| decode-csv(hasHeader="true", separator="\t")
| fix(FLUX_DIR + "langSciPress_csv.fix", *)
| literal-to-object
| open-http
| as-records
| match(pattern="(name=\"DC)\\.(.*?)\\.(.*?)(\")", replacement="$1_$2_$3$4")
| match(pattern="(name=\"DC)\\.(.*?)(\")", replacement="$1_$2$3")
| read-string
| decode-html
| fix(FLUX_DIR + "langSciPress.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
