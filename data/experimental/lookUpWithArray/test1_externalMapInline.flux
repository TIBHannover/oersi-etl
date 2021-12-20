infile = FLUX_DIR + "animals.json";

infile
| open-file
| as-records
| decode-json
| fix(FLUX_DIR + "externalMapInLine.fix")
| encode-json(prettyPrinting="true")
| print
;