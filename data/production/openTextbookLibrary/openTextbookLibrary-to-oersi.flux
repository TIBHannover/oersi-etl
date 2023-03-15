service_domain = "https://open.umn.edu/opentextbooks/";
service_id = "https://open.umn.edu/opentextbooks/";
service_name = "Open Textbook Library";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value


"https://open.umn.edu/opentextbooks/textbooks?page=1"
| oersi.JsonApiReader(method="get", recordPath="data", pageParam="page", stepSize="1", totalLimit="-1")
| decode-json
| fix(FLUX_DIR + "openTextbookLibrary.fix",*)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
