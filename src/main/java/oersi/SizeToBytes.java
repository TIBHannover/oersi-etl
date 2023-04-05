package oersi;

import java.util.List;
import java.util.Map;

import org.metafacture.metafix.Metafix;
import org.metafacture.metafix.Record;
import org.metafacture.metafix.Value;
import org.metafacture.metafix.api.FixFunction;

/**
 * Format human readable data sizes (e.g. '1 MB') as bytes.
 *
 * @author Fabian Steeg
 *
 */
public class SizeToBytes implements FixFunction {

    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final long GB = KB * MB;
    private static final long TB = KB * GB;

    @Override
    public void apply(Metafix metafix, Record theRecord, List<String> params,
            Map<String, String> options) {
        String fieldName = params.get(0);
        if (theRecord.get(fieldName) != null) {
            String oldValue = theRecord.get(fieldName).asString();
            String newValue = String.format("%.0f", bytes(oldValue));
            theRecord.set(fieldName, new Value(newValue));
        }
    }

    public static double bytes(String size) {
        String[] sizeAndUnit = size.split(" ");
        double sizeNumber = Double.parseDouble(sizeAndUnit[0]);
        switch (sizeAndUnit[1]) {
        case "kB":
            return sizeNumber * KB;
        case "MB":
            return sizeNumber * MB;
        case "GB":
            return sizeNumber * GB;
        case "TB":
            return sizeNumber * TB;
        default:
            return sizeNumber;
        }
    }

}
