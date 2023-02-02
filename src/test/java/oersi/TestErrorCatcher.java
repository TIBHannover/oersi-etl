package oersi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;
import org.metafacture.framework.MetafactureException;

/**
 * Tests for {@link ErrorCatcher}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestErrorCatcher {

    private ErrorCatcher<Object> catcher;
    private File file;

    public TestErrorCatcher() throws IOException {
        file = File.createTempFile("oersi-test", ".txt");
    }

    @Test
    public void testShouldWriteErrorsToFile() throws IOException {
        catcher = new ErrorCatcher<Object>(file.getAbsolutePath());
        catcher.process(null);
        catcher.closeStream();
        Assert.assertTrue(Files.readAllLines(file.toPath()).size() > 0);
    }

    @Test(expected = MetafactureException.class)
    public void testShouldHandleWriterCreationFail() throws IOException {
        new ErrorCatcher<Object>("");
    }

    @Test(expected = MetafactureException.class)
    public void testShouldHandleWriteFail() throws IOException {
        catcher = new ErrorCatcher<Object>(file.getAbsolutePath());
        catcher.writer = null;
        catcher.process(null);
    }
}
