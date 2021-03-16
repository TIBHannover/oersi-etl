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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.framework.MetafactureException;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pgssoft.httpclient.HttpClientMock;

/**
 * Tests for {@link OersiWriter}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestOersiWriter {

    private static final String API = "http://localhost";
    private OersiWriter oersiWriter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        oersiWriter = new OersiWriter(API);
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onPost(API).doReturnStatus(400);
        oersiWriter.client = httpClientMock;
    }

    @Test
    public void testShouldWriteResponseToFile() throws IOException {
        File file = File.createTempFile("oersi", ".test");
        file.deleteOnExit();
        oersiWriter.setLog(file.getAbsolutePath());
        oersiWriter.process("{}");
        oersiWriter.closeStream();
        Assert.assertTrue(Files.readAllLines(file.toPath()).size() > 0);
    }

    @Test
    public void testShouldResetCounts() throws JsonProcessingException {
        oersiWriter.process("{}");
        Assert.assertEquals(1, oersiWriter.fail);
        oersiWriter.resetStream();
        Assert.assertEquals(0, oersiWriter.fail);
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchMissingFile() throws JsonProcessingException {
        oersiWriter.setLog("");
    }

    @Test(expected = MetafactureException.class)
    public void testShouldCatchConnectionRefused() {
        oersiWriter = new OersiWriter("http://invalid");
        oersiWriter.process("{}");
    }
}
