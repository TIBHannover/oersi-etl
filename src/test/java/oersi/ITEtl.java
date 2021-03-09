/*
 * Copyright 2021 Fabian Steeg, hbz
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oersi;

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
public class ITEtl {

    private static final Object[][] PARAMS = new Object[][] { //
            // run all *.flux in a given directory:
            new Object[] { Arrays.asList("data/experimental") }, //
            // run a single *.flux file:
            new Object[] { Arrays.asList("data/production/oernds-to-oersi.flux") }, //
            // pass variables as command line arguments:
            new Object[] { Arrays.asList(//
                    "data/production/oernds-to-oersi.flux", //
                    "input_limit=2", //
                    "input_from=5", //
                    "output_schema=resource:/schemas/schema.json", //
                    "backend_api=http://192.168.98.115:8080/oersi/api/metadata", //
                    "backend_user=test", //
                    "backend_pass=test") }, //
            // pass variables as *.properties file:
            new Object[] { Arrays.asList(//
                    "data/production/zoerr-to-oersi.flux", //
                    "data/production/oersi.properties") } //
    };

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> args() {
        return Arrays.asList(PARAMS);
    }

    private List<String> args;

    public ITEtl(List<String> args) {
        this.args = args;
    }

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
    }

    @Test
    public void testConvertMain() throws IOException, SAXException {
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(args.toArray(new String[0]));
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }
}
