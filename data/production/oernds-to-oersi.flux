service_domain="www.oernds.de";
service_id="https://oerworldmap.org/resource/urn:uuid:51277cb8-c5a4-4204-aeaf-10ee06df53ce";
service_name="OERNDS";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value

"https://www.oernds.de/edu-sharing/eduservlet/sitemap?from=0"
| open-http
| oersi.SitemapReader(wait="500",limit=input_limit,urlPattern=".*/components/.*",findAndReplace="https://www.oernds.de/edu-sharing/components/render/(.*)`https://www.oernds.de/edu-sharing/rest/node/v1/nodes/-home-/$1/metadata?propertyFilter=-all-")
| open-http(accept="application/json")
| as-lines
| decode-json
| fix(FLUX_DIR + "edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "oernds-metadata.json", header="[\n", footer="\n]", separator=",\n")
  }{
    oersi.OersiWriter(backend_api,
      user=backend_user, pass=backend_pass, log=FLUX_DIR + "oernds-responses.json")
};
