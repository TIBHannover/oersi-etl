"https://getinfo.tib.eu/oai/intern/repository/tib"
| open-oaipmh(metadataPrefix="datacite", dateFrom="2020-03-06", dateUntil="2020-03-16", setSpec="kmo-av")
| decode-xml
| split-xml-elements(elementname="record", xmldeclaration="")
| literal-to-object
| read-string
| decode-html(attrValsAsSubfields="title.titletype&subject.subjectscheme")
| fix("
map('html.body.record.metadata.resources.resource.titles.title.value', 'name')

do entity('keywords[]', flushWith: 'record')
 map('html.body.record.metadata.resources.resource.subjects.subject.value')
end

do map('html.body.record.metadata.resources.resource.subjects.subject.knm', '@knm')
  not_equals(string: '')
end

do map('html.body.record.metadata.resources.resource.subjects.subject.linsearch', '@linsearch')
  not_equals(string: '')
end

do entity('about[]', flushWith: 'record')
 do entity('')
   do entity('prefLabel')
    map('@linsearch', 'de')
   end
   do map('@linsearch', 'id')
    compose(prefix: 'linsearch://') /* TODO: linsearch ID lookup */
   end
 end
 do entity('')
   do entity('prefLabel')
    map('@knm', 'de')
   end
   do map('@knm', 'id')
    compose(prefix: 'knm://') /* TODO: knm ID lookup */
   end
 end
end
", *)
| encode-json(prettyPrinting="true")
| write(FLUX_DIR + "tibav-data-datacite.json", header="[\n", footer="\n]", separator=",\n")
;