service_domain="redaktion.openeduhub.net";
service_id="https://redaktion.openeduhub.net/edu-sharing/components/collections?id=3bf248cc-fb53-4f81-a99a-a40d6d3d2e71";
service_name="ComeIn.nrw";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_from = "0";
default input_wait = "50";

"https://redaktion.openeduhub.net/edu-sharing/rest/search/v1/queries/local/mds_oeh/ngsearch?contentType=FILES&maxItems=10&skipCount=0&sortProperties=score&sortProperties=cm%3Amodified&sortAscending=false&sortAscending=false&propertyFilter=-all-"
| oersi.JsonApiReader(header=user_agent_header, method="post", body="{\"criteria\": [
    {
        \"property\": \"ccm:oeh_publisher_combined\",
        \"values\": [
            \"ComeIn\"
        ]
    }
], \"facets\": [
    \"ccm:oeh_lrt_aggregated\",
    \"ccm:educationalcontext\",
    \"ccm:educationalintendedenduserrole\",
    \"cclom:general_keyword\",
    \"ccm:taxonid\"
]}", recordPath="nodes", pageParam="skipCount", stepSize="10", totalLimit=input_limit)
| decode-json
| filter-null-values
// edu-sharing WLO
| fix(FLUX_DIR + "comein_edu-sharing.fix", *) // '*': pass all flux variables to the fix
| encode-json
//| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;