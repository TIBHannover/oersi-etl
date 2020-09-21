package oersi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.metafacture.runner.Flux;

/**
 * Run given *.flux file or all *.flux files in the given directory (or in
 * {@link DATA_DIR}, if none are given) with given variables as key=val
 * arguments or the location of a *.properties file (or
 * {@link DEFAULT_PROPERTIES} next to the *.flux file, if none are given),
 * collect *.ndjson output in oersi.ndjson.
 *
 * @author Fabian Steeg (fsteeg)
 *
 */
public class ETL {

    static final String DATA_DIR = "data/experimental";
    static final File OUT_FILE = new File(DATA_DIR, "oersi.ndjson");
    private static final String DEFAULT_PROPERTIES = "oersi.properties";

    private static final Logger LOG = Logger.getLogger(ETL.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[] { DATA_DIR };
        }
        File fileOrDir = new File(args[0]);
        List<String> vars = varsFromCliParams(args);
        List<File> files = fileOrDir.isDirectory()
                ? Arrays.asList(fileOrDir.listFiles((d, f) -> f.toLowerCase().endsWith(".flux")))
                : Arrays.asList(fileOrDir);
        try {
            for (File flux : files) {
                run(flux, vars);
            }
            writeTestOutput(fileOrDir);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static List<String> varsFromCliParams(String[] args) {
        List<String> vars = new ArrayList<>();
        if (args.length > 1) {
            if (args[1].endsWith(".properties")) {
                vars = varsFromProperties(new File(args[1]));
            } else {
                vars.addAll(Arrays.asList(args).subList(1, args.length));
            }
        }
        return vars;
    }

    private static List<String> varsFromProperties(File file) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());
    }

    private static void run(File flux, List<String> vars) throws Exception {
        List<String> args = new ArrayList<>(vars);
        File defaultProperties = new File(flux.getParent(), DEFAULT_PROPERTIES);
        if (args.isEmpty() && defaultProperties.exists()) {
            args = varsFromProperties(defaultProperties);
        }
        args.add(0, flux.getAbsolutePath());
        LOG.log(Level.INFO, "Running {0}", new Object[] { args });
        Flux.main(args.toArray(new String[] {}));
    }

    private static void writeTestOutput(File fileOrDir) throws IOException {
        try (FileWriter w = new FileWriter(OUT_FILE)) {
            if (fileOrDir.isDirectory()) {
                for (File json : fileOrDir.listFiles((d, f) -> f.toLowerCase().endsWith(".ndjson")
                        && !f.equalsIgnoreCase(OUT_FILE.getName()))) {
                    writeSingleFile(w, json);
                }
            } else {
                File singleOut = new File(fileOrDir.getParent(),
                        fileOrDir.getName().split("\\.")[0] + ".ndjson");
                if (singleOut.exists()) {
                    writeSingleFile(w, singleOut);
                }
            }
        }
    }

    private static void writeSingleFile(FileWriter w, File json) throws IOException {
        LOG.log(Level.INFO, "Writing {0} to {1}", new Object[] { json, OUT_FILE });
        String bulk = Files.readAllLines(Paths.get(json.toURI())).stream()
                .collect(Collectors.joining("\n"));
        w.write(bulk);
        w.write("\n");
    }
}
