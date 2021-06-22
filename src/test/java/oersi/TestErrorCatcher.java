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
import org.junit.Test;
import org.metafacture.framework.MetafactureException;

/**
 * Tests for {@link ErrorCatcher}.
 *
 * @author Fabian Steeg
 *
 */
public final class TestErrorCatcher {

    private ErrorCatcher<Object> catcher;
    private File file;

    public TestErrorCatcher() throws IOException {
        file = File.createTempFile("oersi-test", ".txt");
    }

    @Test
    public void testShouldWriteErrorsToFile() throws IOException {
        catcher = new ErrorCatcher<Object>(file.getAbsolutePath());
        catcher.process(null);
        catcher.closeStream();
        Assert.assertTrue(Files.readAllLines(file.toPath()).size() > 0);
    }

    @Test(expected = MetafactureException.class)
    public void testShouldHandleWriterCreationFail() throws IOException {
        new ErrorCatcher<Object>("");
    }

    @Test(expected = MetafactureException.class)
    public void testShouldHandleWriteFail() throws IOException {
        catcher = new ErrorCatcher<Object>(file.getAbsolutePath());
        catcher.writer = null;
        catcher.process(null);
    }
}
