service_domain = "https://www.hoou.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:ac5d5269-6449-43c0-b43b-2ed372763a0e";
service_name = "HOOU";

default input_limit = "10"; // 'default': is overridden by command-line/properties value
default input_wait = "20";

"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern=".*/(materials|projects)/.*")
| object-to-literal
| encode-json
| decode-json
| fix(FLUX_DIR + "hoou-id.fix", *)
| stream-to-triples
| @X;

"https://www.hoou.de/sitemap.xml" // FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern=".*/(materials|projects)/.*")
| open-http
| extract-element("script[data-test=model-linked-data]")
| decode-json
| fix(FLUX_DIR + "hoou.fix", *)
| stream-to-triples
| @X;

@X
| wait-for-inputs("2")
| sort-triples(by="subject")
| collect-triples
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "hoou-metadata.json", header="[\n", footer="\n]", separator=",\n")
  };