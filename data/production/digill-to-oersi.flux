service_domain = "https://digill.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:ad0b2478-a9fe-46fb-81ef-56ccec8fa2ae";
service_name = "digiLL";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://digill.de/course-sitemap.xml" // FLUX_DIR + "digiLL-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern=".*/course/.*")
| open-http
| extract-element("script[class=yoast-schema-graph]")
| decode-json
| fix(FLUX_DIR + "digill.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "digill-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter(backend_api,
      user=backend_user, pass=backend_pass, log=FLUX_DIR + "digill-responses.json")
  };