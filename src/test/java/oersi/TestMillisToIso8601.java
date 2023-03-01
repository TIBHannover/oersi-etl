package oersi;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.metafacture.metafix.Record;
import org.metafacture.metafix.Value;

/**
 * Tests for {@link MillisToIso8601}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestMillisToIso8601 {

    @Test
    public void millisToIso8601() {
        Record record = new Record();
        record.put("duration", new Value("83926"));
        new MillisToIso8601().apply(null, record, Arrays.asList("duration"), null);
        Assert.assertEquals("PT1M23.926S", record.get("duration").asString());
    }
}
