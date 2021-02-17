service_domain = "https://www.hoou.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:ac5d5269-6449-43c0-b43b-2ed372763a0e";
service_name = "HOOU";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

// the embedded json is missing the URL as id-metadata.
// following approach handles the URL form SitemapReader as a separate data source 
// and merges it with one embedded JSON document.

"https://www.hoou.de/sitemap.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern=".*/(materials|projects)/.*")
| object-to-literal(literalName="id", recordId="%d")
| fix(FLUX_DIR + "hoou-id.fix", *)
| stream-to-triples
| @X;

"https://www.hoou.de/sitemap.xml" // for local testing: "file://" + FLUX_DIR + "hoou-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit=input_limit, urlPattern=".*/(materials|projects)/.*")
| catch-object-exception
| open-http
| extract-element("script[data-test=model-linked-data]")
| decode-json
| fix(FLUX_DIR + "hoou.fix", *)
| filter-null-values
| stream-to-triples
| @X;

@X
| wait-for-inputs("2")
| sort-triples(by="subject")
| collect-triples
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;