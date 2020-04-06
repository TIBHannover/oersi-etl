package indexer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

@RunWith(Parameterized.class)
public class IndexerTestMain {

    private static final Object[][] PARAMS = new Object[][] { //
            new Object[] { Arrays.asList() }, //
            new Object[] { Arrays.asList("1") }, //
            new Object[] { Arrays.asList("1", "2") } };

    @Parameterized.Parameters
    public static Collection<Object[]> args() {
        return Arrays.asList(PARAMS);
    }

    private List<String> args;

    public IndexerTestMain(List<String> args) {
        this.args = args;
    }

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(Indexer.OUT_FILE.toURI()));
    }

    @Test
    public void testConvertMain() throws IOException, SAXException {
        assertFalse(Indexer.OUT_FILE.exists());
        Indexer.main(args.toArray(new String[0]));
        assertTrue(Indexer.OUT_FILE.exists());
    }
}
