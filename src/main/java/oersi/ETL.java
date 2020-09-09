package oersi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.metafacture.runner.Flux;

/**
 * Run all *.flux workflows in the given directories (or in DATA_DIR, if none
 * are given), collect *.ndjson output in oersi.ndjson
 *
 * @author Fabian Steeg (fsteeg)
 *
 */
public class ETL {

    static final String DATA_DIR = "data/experimental";
    static final File OUT_FILE = new File(DATA_DIR, "oersi.ndjson");

    private static final Logger LOG = Logger.getLogger(ETL.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[] { DATA_DIR };
        }
        for (String arg : args) {
            File file = new File(arg);
            try {
                for (File flux : file.listFiles((d, f) -> f.toLowerCase().endsWith(".flux"))) {
                    String fluxText = Files.readAllLines(Paths.get(flux.toURI())).stream()
                            .collect(Collectors.joining("\n"));
                    LOG.log(Level.INFO, "Running {0}: \n{1}", new Object[] { flux, fluxText });
                    Flux.main(new String[] { flux.getAbsolutePath() });
                }
                try (FileWriter w = new FileWriter(OUT_FILE)) {
                    for (File json : file.listFiles((d, f) -> f.toLowerCase().endsWith(".ndjson")
                            && !f.equalsIgnoreCase(OUT_FILE.getName()))) {
                        LOG.log(Level.INFO, "Writing {0} to {1}", new Object[] { json, OUT_FILE });
                        String bulk = Files.readAllLines(Paths.get(json.toURI())).stream()
                                .collect(Collectors.joining("\n"));
                        w.write(bulk);
                        w.write("\n");
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}
