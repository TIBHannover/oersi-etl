<<<<<<< HEAD
service_domain = "https://digill.de/";
service_id = "https://https://oerworldmap.org/resource/urn:uuid:ad0b2478-a9fe-46fb-81ef-56ccec8fa2ae";
service_name = "digiLL";


=======
>>>>>>> 6022a6ce935913bd2e122ba2ef7102ee74095595
"https://digill.de/course-sitemap.xml" // FLUX_DIR + "digiLL-sitemap.xml"
| oersi.SitemapReader(wait="10",limit="2",urlPattern=".*/course/.*")
| log-object("Open: ")
| open-http
| extract-element("script[class=yoast-schema-graph]")
//| log-object("JSON in: ")
| decode-json
//| log-stream("Event in: ")
| fix(FLUX_DIR + "digiLL.fix")
//| log-stream("Event out: ")
| encode-json
| log-object("JSON out: ")
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "digiLL-metadata.json", header="[\n", footer="\n]", separator=",\n")
<<<<<<< HEAD
  }{
    oersi.OersiWriter(backend_api,
      user=backend_user, pass=backend_pass, log=FLUX_DIR + "digiLL-responses.json")
};
=======
  };
>>>>>>> 6022a6ce935913bd2e122ba2ef7102ee74095595
