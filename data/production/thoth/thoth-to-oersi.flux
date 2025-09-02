service_id = "https:/https://thoth.pub";
service_name = "Thoth";

default input_limit = "-1"; // 'default': is overridden by command-line/properties value
default input_wait = "50";
QUERY = "\"query GetTextbooks { works(workTypes: TEXTBOOK, limit: 9999) { landingPage workId fullTitle doi publications {   publicationType   isbn } contributions {   contributor {  orcid   }   contributionType   fullName   affiliations {  institution {    institutionName    institutionId    ror  }   } } longAbstract workType workStatus license publicationDate languages {   languageCode   languageRelation   mainLanguage } publications {   publicationType   isbn   locations {  fullTextUrl  landingPage   } } subjects {   subjectCode   subjectId } imprint {   imprintName   imprintUrl   publisher {  publisherName  publisherUrl   } } coverUrl  }}\"";

"https://api.thoth.pub/graphql"
| open-http(method="POST", contentType="application/json", body="{ \"query\":" + QUERY + "}", header=user_agent_header)
| as-lines
| decode-json(recordPath="$.data.works")
| filter-null-values
| fix(FLUX_DIR + "thoth.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
