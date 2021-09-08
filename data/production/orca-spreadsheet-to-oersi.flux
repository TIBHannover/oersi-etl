service_domain = "ORCA-Dummi";
service_id = "https://oerworldmap.org/resource/urn:uuid:ORCA-DUMMI";
service_name = "ORCA-Dummi";

"https://docs.google.com/spreadsheets/d/16fN1OehuPx61n2O6ial5fYc1h6jJIlPj93SkhBpfypU/gviz/tq?tqx=out:csv&sheet=Tabellenblatt1"
| open-http(accept="application/csv")
| as-lines
| decode-csv(hasHeader="true")
| fix(FLUX_DIR + "orca-csv.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
