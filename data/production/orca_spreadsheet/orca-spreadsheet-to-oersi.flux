service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";

"https://docs.google.com/spreadsheets/d/16fN1OehuPx61n2O6ial5fYc1h6jJIlPj93SkhBpfypU/gviz/tq?tqx=out:csv&sheet=Tabellenblatt1"
| open-http(header=user_agent_header, accept="application/csv")
| as-lines
| match(pattern="(Urheber\\d|Beitragende\\d)\\.", replacement="$1_")
| decode-csv(hasHeader="true")
| fix(FLUX_DIR + "orca-csv.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
