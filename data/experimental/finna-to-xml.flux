"https://api.finna.fi/api/v1/search?field%5B%5D=fullRecord&field%5B%5D=id&filter%5B%5D=building%3A0%2Faoe%2F&limit=2&lng=en-gb"
| open-http(header=user_agent_header, accept="application/json")
| as-lines
| decode-json
| org.metafacture.metamorph.Metafix("map('records[].*.fullRecord')")
| literal-to-object
| write(FLUX_DIR + "finna.xml", header="<records>\n", footer="\n</records>", separator="\n")
;