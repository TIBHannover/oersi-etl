// Get from OAI-PMH, write to local file for testing:

XML_FILE = FLUX_DIR + "duepub-metafacture.xml";

//"https://duepublico2.uni-due.de/oer/oai"
//| open-oaipmh(metadataPrefix="mods",dateFrom="2021-05-14",dateUntil="2021-05-31")
//| as-lines
//| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "empty.fix")
| flatten
//| morph(FLUX_DIR + "uniqueFields.xml") //This MORPH unqiues the fields in a record.
|	stream-to-triples
| count-triples(countBy="PREDICATE")
| sort-triples(By="SUBJECT")
| template("${o}\t${s}")
| write("stdout")
;