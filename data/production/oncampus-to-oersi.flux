service_domain = "https://www.oncampus.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:4acc8053-05a4-4890-9e99-81af4a831933";
service_name = "oncampus";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://www.oncampus.de/sitemap.xml" // FLUX_DIR + "oncampus-sitemap.xml"
| oersi.SitemapReader(wait=input_wait,limit=input_limit)
| open-http
| decode-html(attrValsAsSubfields="&h3.class")
| fix-filter("
do map('*.h3.productcost')
    regexp(match: 'free|kostenlos')
end")
| fix(FLUX_DIR + "oncampus.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "oncampus-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter(backend_api,
      user=backend_user, pass=backend_pass, log=FLUX_DIR + "oncampus-responses.json")
};