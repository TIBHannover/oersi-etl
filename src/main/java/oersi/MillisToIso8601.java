package oersi;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.metafacture.metafix.Metafix;
import org.metafacture.metafix.Record;
import org.metafacture.metafix.Value;
import org.metafacture.metafix.api.FixFunction;

/**
 * Format durations in milliseconds according to ISO 8601.
 *
 * @author Fabian Steeg
 *
 */
public class MillisToIso8601 implements FixFunction {

    @Override
    public void apply(Metafix metafix, Record theRecord, List<String> params,
            Map<String, String> options) {
        String fieldName = params.get(0);
        if (theRecord.get(fieldName) != null) {
            String oldValue = theRecord.get(fieldName).asString();
            String newValue = Duration.ofMillis(Long.parseLong(oldValue)).toString();
            theRecord.set(fieldName, new Value(newValue));
        }
    }

}
