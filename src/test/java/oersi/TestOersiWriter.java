package oersi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.MetafactureException;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pgssoft.httpclient.HttpClientMock;

/**
 * Tests for {@link OersiWriter}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestOersiWriter {

    private static final String API = "http://localhost";
    private static final int BULK_SIZE = 20;
    private OersiWriter oersiWriter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        oersiWriter = new OersiWriter(API);
        oersiWriter.setBulkSize(BULK_SIZE);
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onPost(API + "/bulk").doReturnStatus(400);
        oersiWriter.client = httpClientMock;
    }

    @Test
    public void testShouldWriteResponseToFile() throws IOException {
        File file = File.createTempFile("oersi", ".test");
        file.deleteOnExit();
        oersiWriter.setLog(file.getAbsolutePath());
        oersiWriter.process("{}");
        oersiWriter.closeStream();
        Assert.assertTrue(Files.readAllLines(file.toPath()).size() > 0);
        Assert.assertEquals(BULK_SIZE, oersiWriter.fail);
    }

    @Test
    public void testShouldResetCounts() throws JsonProcessingException {
        oersiWriter.setBulkSize(1);
        oersiWriter.process("{}");
        Assert.assertEquals(1, oersiWriter.fail);
        oersiWriter.resetStream();
        Assert.assertEquals(0, oersiWriter.fail);
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchMissingFile() throws JsonProcessingException {
        oersiWriter.setLog("");
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchConnectionRefused() {
        oersiWriter = new OersiWriter("http://invalid");
        oersiWriter.process("{}");
        oersiWriter.closeStream();
    }
}
