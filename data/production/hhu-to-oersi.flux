service_domain = "https://mediathek.hhu.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:b6c2027f-b747-4c66-9cd8-1e363659af9b";
service_name = "HHU Mediathek";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://mediathek.hhu.de/sitemap?list=videos" // FLUX_DIR + "hhu-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit)
| open-http
| decode-html
| fix(FLUX_DIR + "hhu.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "hhu-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter(backend_api,
      user=backend_user, pass=backend_pass, log=FLUX_DIR + "hhu-responses.json")
};