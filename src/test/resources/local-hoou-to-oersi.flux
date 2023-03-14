"file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait="50", limit="2", urlPattern=".*/(materials|projects)/.*")
| object-to-literal(literalName="id", recordId="%d")
| encode-json
| oersi.JsonValidator("https://gitlab.com/oersi/oersi-schema/-/raw/main/schemas/metadata/schema.json", "https://gitlab.com/oersi/oersi-schema/-/raw/main/schemas/metadata/", writeValid="local-metadata.json", writeInvalid="local-invalid.json")
| object-tee | {
     write(FLUX_DIR + "hoou.ndjson")
  }{
    oersi.OersiWriter("http://localhost", user="test", pass="test", log=FLUX_DIR + "local-responses.json")
};
