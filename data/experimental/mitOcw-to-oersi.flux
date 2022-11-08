"https://open.mit.edu/api/v0/search/"
| oersi.JsonApiReader(method="post", recordPath="hits.hits", pageParam="from", stepSize="10", totalLimit="15", body=
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
