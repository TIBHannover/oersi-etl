"https://api.finna.fi/api/v1/search?field%5B%5D=fullRecord&field%5B%5D=id&filter%5B%5D=building%3A0%2Faoe%2F&limit=2&lng=en-gb"
| open-http(accept="application/json")
| as-lines
| decode-json
| fix("map('records[].*.fullRecord')")
| literal-to-object
| read-string
| decode-html(attrValsAsSubfields="title.lang&description.lang")
| fix("
map('*.title.en', 'name')
map('*.description.en', 'description')")
| encode-json
| write(FLUX_DIR + "finna-metadata.json", header="[\n", footer="\n]", separator=",\n")
;