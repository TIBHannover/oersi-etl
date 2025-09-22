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
 * Tests for {@link HtmlToText}.
 *
 * @author Tobias Bülte
 *
 */
@RunWith(Parameterized.class)
public final class TestHtmlToText {

    private static final Object[][] PARAMS = new Object[][] { //
            { "<b>Hello World.</b><br/><p><i>Is there anyone out there?</i><p>", "Hello World. Is there anyone out there?" }, //
            { "<br/>Grundwissen<br/>Basisfunktionen<br/>Fortgeschrittene Funktionen<br/>Zus&auml;tzliche Tipps<br/>", "\nGrundwissen Basisfunktionen\nFortgeschrittene Funktionen\nZusätzliche Tipps" }, //
    };

    @Parameterized.Parameters(name = "{0} -> {1}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String in;
    private String out;

    public TestHtmlToText(String in, String out) {
        this.in = in;
        this.out = out;
    }

    @Test
    public void HtmlToText() {
        Record record = new Record();
        record.add("description[]", Value.newArray());
        record.addNested("description[].$append.content", new Value(in));
        new HtmlToText().apply(null, record, Arrays.asList("description[].$last.content"), null);
        Assert.assertEquals(out, record.get("description[].$last.content").asString());
    }
}
