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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
