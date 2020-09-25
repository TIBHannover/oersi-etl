service_domain = "www.oerbw.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:4062c64d-b0ac-4941-95c2-8116f137326d";
service_name = "ZOERR";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "{0,500}"; // repeat sitemap crawl: once with 0, once with 500

"https://www.oerbw.de/edu-sharing/eduservlet/sitemap?from=" + input_from
| oersi.SitemapReader(wait="500",limit=input_limit,urlPattern=".*/components/.*",findAndReplace="https://uni-tuebingen.oerbw.de/edu-sharing/components/render/(.*)`https://uni-tuebingen.oerbw.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| as-lines
| decode-json
| fix(FLUX_DIR + "edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "oerbw-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter(backend_api,
      user=backend_user, pass=backend_pass, log=FLUX_DIR + "oerbw-responses.json")
};
