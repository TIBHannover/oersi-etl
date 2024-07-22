/* Copyright 2013-2024 Pascal Christoph and others.
 * Licensed under the Eclipse Public License 1.0 */

package oersi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.oclc.oai.harvester2.verb.ListRecords;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Opens an OAI-PMH stream and passes a reader to the receiver.
 *
 * @author Pascal Christoph (dr0i)
 *
 */
@Description("Opens an OAI-PMH stream and passes a reader to the receiver. Mandatory arguments are: BASE_URL, DATE_FROM, DATE_UNTIL, METADATA_PREFIX, SET_SPEC .")
@In(String.class)
@Out(java.io.Reader.class)
public final class OaiPmhOpener extends DefaultObjectPipe<String, ObjectReceiver<Reader>> {

    private String encoding = "UTF-8";

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private String dateFrom;

    private String dateUntil;

    private String setSpec;

    private String metadataPrefix;

    /**
     * Default constructor
     */
    public OaiPmhOpener() {
    }

    /**
     * Sets the encoding to use. The default setting is UTF-8.
     *
     * @param encoding new default encoding
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets the beginning of the retrieving of updated data. The form is YYYY-MM-DD
     * .
     *
     * @param dateFrom The form is YYYY-MM-DD .
     */
    public void setDateFrom(final String dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * Sets the end of the retrieving of updated data. The form is YYYY-MM-DD .
     *
     * @param dateUntil The form is YYYY-MM-DD .
     */
    public void setDateUntil(final String dateUntil) {
        this.dateUntil = dateUntil;
    }

    /**
     * Sets the OAI-PM metadata prefix .
     *
     * @param metadataPrefix the OAI-PM metadata prefix
     */
    public void setMetadataPrefix(final String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    /**
     * Sets the OAI-PM set specification .
     *
     * @param setSpec the OAI-PM set specification
     */
    public void setSetSpec(final String setSpec) {
        this.setSpec = setSpec;
    }

    @Override
    public void process(final String baseUrl) {
        try {
            listRecords(baseUrl);
            getReceiver().process(new InputStreamReader(
                    new ByteArrayInputStream(outputStream.toByteArray()), encoding));
        } catch (final Exception e) {
            throw new MetafactureException(e);
        }
    }

    private void listRecords(final String baseUrl) throws Exception {
        // This is part of the code previously called via RawWrite.run, see
        // https://github.com/metafacture/metafacture-core/blob/master/metafacture-biblio/src/main/java/org/metafacture/biblio/OaiPmhOpener.java#L103
        // https://github.com/DSpace/oclc-harvester2/blob/main/src/main/java/org/oclc/oai/harvester2/app/RawWrite.java#L108
        outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
        outputStream.write("<harvest>\n".getBytes("UTF-8"));
        ListRecords listRecords = new ListRecords(baseUrl, this.dateFrom, this.dateUntil,
                this.setSpec, this.metadataPrefix);
        while (listRecords != null) {
            NodeList errors = listRecords.getErrors();
            if (errors != null && errors.getLength() > 0) {
                System.err.println("Found errors");
                int length = errors.getLength();
                for (int i = 0; i < length; ++i) {
                    Node item = errors.item(i);
                    System.out.println(item);
                }
                System.err.println("Error record: " + listRecords.toString());
                break;
            }
            outputStream.write(listRecords.toString().getBytes("UTF-8"));
            outputStream.write("\n".getBytes("UTF-8"));
            String resumptionToken = listRecords.getResumptionToken();
            System.out.println("resumptionToken: " + resumptionToken);
            if (resumptionToken == null || resumptionToken.length() == 0) {
                listRecords = null;
            } else {
                listRecords = new ListRecords(baseUrl, resumptionToken);
            }
        }
        outputStream.write("</harvest>\n".getBytes("UTF-8"));
    }
}
