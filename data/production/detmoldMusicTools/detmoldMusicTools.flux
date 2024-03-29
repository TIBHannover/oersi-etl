service_id = "https://detmoldmusictools.de/";
service_name = "detmoldMusicTools";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://raw.githubusercontent.com/elena1113/MetadatenDMT/main/Kursliste.txt" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| open-http(header=user_agent_header)
| as-lines
| open-http(header=user_agent_header)
| as-records
| decode-json
| fix(FLUX_DIR + "detmoldMusicTools.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
