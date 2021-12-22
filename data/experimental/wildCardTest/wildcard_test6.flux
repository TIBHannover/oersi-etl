infile = FLUX_DIR + "animals_complex.json";

infile
| open-file
| as-records
| decode-json
| fix(FLUX_DIR + "appendWithAsteriksWildcardAtTheEnd.fix")
| encode-json(prettyPrinting="true")
| print
;