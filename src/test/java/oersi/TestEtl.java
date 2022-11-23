package oersi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

public class TestEtl {

    @Test
    public void testConvertMainOk() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/local-hoou-to-oersi.flux" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainFailEmptyInput() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/failing-empty-input.flux" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainFailEmptyOutput() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/failing-empty-output.flux" });
        assertTrue("No data should be written to output file: " + ETL.OUT_FILE,
                Files.readAllLines(Paths.get(ETL.OUT_FILE.toURI())).isEmpty());
    }

    public void testConvertMainFailFixNotFound() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/failing-fix-not-found.flux" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainAllFluxFiles() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] {});
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainPropertiesOk() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/local-hoou-to-oersi.flux",
                "data/production/oersi.properties" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainResetErrors() throws IOException {
        File errors = new File(ETL.DATA_DIR, "failing-errors.txt");
        Files.deleteIfExists(Paths.get(errors.toURI()));
        assertFalse(errors.exists()); // no errors, will now run flux writing errors:
        ETL.main(new String[] { "src/test/resources/failing-write-errors.flux" });
        assertTrue(errors.exists()); // got errors, now without errors, same base name:
        ETL.main(new String[] { "src/test/resources/failing-fix-not-found.flux" });
        assertFalse(errors.exists()); // no errors, both write to same base name file
    }

    @Test
    public void testMaskCredentials() {
        assertArrayEquals("normal values are unmasked",
                new Object[] { Arrays.asList("some_prop=v") },
                ETL.maskCredentials(Arrays.asList("some_prop=v")));
        assertArrayEquals("credentials are masked", new Object[] { Arrays.asList(//
                "some_user=<masked>", "some_pass=<masked>", "some_auth=<masked>") },
                ETL.maskCredentials(Arrays.asList("some_user=v", "some_pass=v", "some_auth=v")));
    }

    @Test(expected = IOException.class)
    public void testConvertMainPropertiesFail() throws IOException {
        ETL.main(new String[] { "src/test/resources/", "non-existing.properties" });
    }
}
