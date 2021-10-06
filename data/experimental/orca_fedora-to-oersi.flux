// For data API documentation see:
// https://api.paideia.hbz-nrw.de/public/docs/index.html#!/resource/listResources
// https://api.paideia.hbz-nrw.de/public/docs/index.html#!/resource/listLrmiData

service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";

FLUX_DIR + "markus.json"
| open-file
| as-records
| decode-json
| fix(FLUX_DIR +  "orca_fedoraNew.fix")
| encode-json
//| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| write(FLUX_DIR + "MarkusOutput.json")
;
