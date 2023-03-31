"https://open.mit.edu/api/v0/search/"
| oersi.JsonApiReader(header=user_agent_header, method="post", recordPath="hits.hits", pageParam="from", pageInBody="true", stepSize="10", totalLimit="15", body=
"{
    \"from\":0,
    \"size\":10,
    \"query\": {
        \"bool\": {
            \"filter\": [
                {\"term\": {\"object_type\": \"course\"}}
            ]
        }
    }
}")
| write(metadata_invalid)
;
