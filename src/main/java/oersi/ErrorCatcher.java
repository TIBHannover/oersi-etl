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

import java.io.FileWriter;
import java.io.IOException;

import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Like `ObjectExceptionCatcher`, but write errors to file for our summary.
 *
 * @author Fabian Steeg
 */
public final class ErrorCatcher<T> extends DefaultObjectPipe<T, ObjectReceiver<T>> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorCatcher.class);

    private FileWriter writer = null;

    public ErrorCatcher(String file) {
        super();
        try {
            this.writer = new FileWriter(file);
        } catch (IOException e) {
            throw new MetafactureException(file, e);
        }
    }

    @Override
    public void process(final T obj) {
        try {
            getReceiver().process(obj);
        } catch (final Exception e) {
            LOG.info(e.getMessage());
            try {
                writer.append(e.getMessage());
                writer.append("\n");
            } catch (IOException e1) {
                throw new MetafactureException(e1);
            }
            LOG.debug(e.getMessage(), e);
        }
    }

    @Override
    public void onCloseStream() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new MetafactureException(e.getMessage(), e);
            }
        }
    }

}
