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

import org.junit.Test;
import org.xml.sax.SAXException;

public class TestEtl {

    @Test
    public void testConvertMainOk() throws IOException, SAXException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/local-hoou-to-oersi.flux" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainFail() throws IOException, SAXException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/failing.flux" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainAllFluxFiles() throws IOException, SAXException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] {});
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test
    public void testConvertMainPropertiesOk() throws IOException, SAXException {
        Files.deleteIfExists(Paths.get(ETL.OUT_FILE.toURI()));
        assertFalse(ETL.OUT_FILE.exists());
        ETL.main(new String[] { "src/test/resources/local-hoou-to-oersi.flux",
                "data/production/oersi.properties" });
        assertTrue("Output file must exist: " + ETL.OUT_FILE, ETL.OUT_FILE.exists());
    }

    @Test(expected = IOException.class)
    public void testConvertMainPropertiesFail() throws IOException, SAXException {
        ETL.main(new String[] { "src/test/resources/", "non-existing.properties" });
    }
}
