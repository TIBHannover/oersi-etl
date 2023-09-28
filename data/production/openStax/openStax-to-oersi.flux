service_domain = "https://openstax.org/";
service_id = "https://openstax.org/";
service_name = "openstax";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://openstax.org/apps/cms/api/v2/pages/?type=books.Book&fields=title,id,book_state&limit=1000"
| open-http(accept="application/json")
| as-records
| decode-json(recordPath="$.items")
| fix("copy_field('meta.detail_url','detail_url') retain('detail_url')" )
| literal-to-object
| open-http(accept="application/json")
| as-records
| decode-json
| fix(FLUX_DIR + "openStax.fix",*)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
