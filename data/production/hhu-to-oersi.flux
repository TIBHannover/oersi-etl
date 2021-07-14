service_domain = "https://mediathek.hhu.de/";
service_id = "https://oerworldmap.org/resource/urn:uuid:b6c2027f-b747-4c66-9cd8-1e363659af9b";
service_name = "HHU Mediathek";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://mediathek.hhu.de/sitemap?list=videos" // FLUX_DIR + "hhu-sitemap.xml"
| oersi.SitemapReader(wait=input_wait, limit="30")
| open-http
| decode-html(attrValsAsSubfields="&p.class&a.class&div.class&span.class")
| morph(FLUX_DIR + "noTitle.xml")
| encode-json
| write("stdout")
;