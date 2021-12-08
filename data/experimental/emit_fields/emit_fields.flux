infile = FLUX_DIR + "underscore.json";

infile
| open-file
| as-records
| decode-json
| fix(FLUX_DIR + "emit_fields.fix")
| encode-json(prettyPrinting="true")
| print
;