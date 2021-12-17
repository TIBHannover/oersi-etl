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
| metafix(FLUX_DIR + "duepub-metafacture.fix")
| encode-json(prettyPrinting="true")
| write(FLUX_DIR + "duepub-metafacture.json");