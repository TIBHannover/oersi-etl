infile = FLUX_DIR + "animalsStrings.json";

infile
| open-file
| as-records
| decode-json
| fix(FLUX_DIR + "test_append_string.fix")
| encode-json(prettyPrinting="true")
| print
;