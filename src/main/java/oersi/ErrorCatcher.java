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

    FileWriter writer = null;

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
            } catch (Exception x) {
                throw new MetafactureException(x.getMessage(), x);
            }
            LOG.debug(e.getMessage(), e);
        }
    }

    @Override
    public void onCloseStream() {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                throw new MetafactureException(e.getMessage(), e);
            }
        }
    }

}
