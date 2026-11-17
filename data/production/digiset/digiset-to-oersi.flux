service_domain = "https://oer.digisetproject.eu/";
service_id = "https://oer.digisetproject.eu/";
service_name = "DigiSET Open Education Resources";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";

"https://oer.digisetproject.eu/wp-sitemap-posts-oer_resource-1.xml" // FLUX_DIR + "hhu-sitemap.xml"
| oersi.SitemapReader(header=user_agent_header, wait=input_wait, limit=input_limit)
| open-http(header=user_agent_header)
| decode-html
| fix("do list(path:'html.head.link','var':'$i')
            if any_match('$i.href','https://oer.digisetproject.eu/wp-json/wp/v2/oer_resource/.*')
                copy_field('$i.href','jsonUrl')
            end
        end
    retain('jsonUrl')")
| literal-to-object
| open-http(accept="application/json")
| as-records
| decode-json
| fix(FLUX_DIR + "digiset.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;