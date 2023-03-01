package oersi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.metafacture.metafix.Record;
import org.metafacture.metafix.Value;

/**
 * Tests for {@link SizeToBytes}.
 *
 * @author Fabian Steeg
 *
 */
@RunWith(Parameterized.class)
public final class TestSizeToBytes {

    private static final Object[][] PARAMS = new Object[][] { //
            { "1 kB", "1024" }, //
            { "1 MB", "1048576" }, //
            { "1 GB", "1073741824" }, //
            { "1 TB", "1099511627776" } //
    };

    @Parameterized.Parameters(name = "{0} -> {1}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String in;
    private String out;

    public TestSizeToBytes(String in, String out) {
        this.in = in;
        this.out = out;
    }

    @Test
    public void sizeToBytes() {
        Record record = new Record();
        record.add("content[]", Value.newArray());
        record.addNested("content[].$append.size", new Value(in));
        new SizeToBytes().apply(null, record, Arrays.asList("content[].$last.size"), null);
        Assert.assertEquals(out, record.get("content[].$last.size").asString());
    }
}
