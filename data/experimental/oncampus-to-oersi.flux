service_domain = "https://www.oncampus.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:4acc8053-05a4-4890-9e99-81af4a831933";
service_name = "oncampus";

default input_limit = "20"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://www.oncampus.de/sitemap.xml" // FLUX_DIR + "oncampus-sitemap.xml"
| oersi.SitemapReader(wait=input_wait,limit=input_limit)
| open-http
| decode-html
| filter(FLUX_DIR + "oncampus_filter.xml") // we need only matching "kostenlos" or "free", but filter-module is crashing
| fix(FLUX_DIR + "oncampus.fix", *)
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "oncampus-metadata.json", header="[\n", footer="\n]", separator=",\n")
  };