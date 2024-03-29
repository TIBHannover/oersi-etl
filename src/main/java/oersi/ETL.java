package oersi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONObject;
import org.metafacture.runner.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static final String DATA_DIR = "src/test/resources";
    static final File OUT_FILE = new File(DATA_DIR, "oersi.ndjson");
    private static final String DEFAULT_PROPERTIES = "oersi.properties";

    private static final Logger LOG = LoggerFactory.getLogger(ETL.class);

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
            LOG.error(e.getMessage(), e);
        }
    }

    private static Object formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(time));
    }

    private static List<String> varsFromCliParams(String[] args) throws IOException {
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

    private static List<String> varsFromProperties(File file) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            properties.load(in);
        }
        return properties.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());
    }

    private static void run(File flux, List<String> vars) throws IOException, RecognitionException {
        String name = flux.getName().split("-")[0];
        File fluxDir = flux.getParentFile();
        File fileProcessErrors = new File(fluxDir, name + "-errors.txt");
        File fileInvalid = new File(fluxDir, name + "-invalid.json");
        File fileValid = new File(fluxDir, name + "-metadata.json");
        File fileResponses = new File(fluxDir, name + "-responses.json");
        Stream.of(fileProcessErrors, fileInvalid, fileValid, fileResponses).forEach(File::delete);
        List<String> fullVars = setUpVars(flux, vars, fileProcessErrors, fileInvalid, fileValid,
                fileResponses);
        long start = System.currentTimeMillis();
        try {
            Flux.main(fullVars.toArray(new String[] {}));
            logSummary(flux, fileProcessErrors, fileInvalid, fileResponses,
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOG.error("Error processing import channel {}", flux.getName(), e);
            LOG.info("Import channel {} FAILED: {} ({})", flux.getName(), e.getMessage(),
                    e.getCause() != null ? e.getCause().getClass().getSimpleName() : "<no cause>");
        }
    }

    private static List<String> setUpVars(File flux, List<String> vars, File fileProcessErrors,
            File fileInvalid, File fileValid, File fileResponses) throws IOException {
        List<String> args = new ArrayList<>(vars);
        File defaultProperties = new File(flux.getParent(), DEFAULT_PROPERTIES);
        if (args.isEmpty() && defaultProperties.exists()) {
            args = varsFromProperties(defaultProperties);
        }
        args.add(0, flux.getAbsolutePath());
        List<String> debuggingOutputLocations = Arrays.asList(//
                "file_errors=" + fileProcessErrors.getAbsolutePath(), //
                "metadata_invalid=" + fileInvalid.getAbsolutePath(), //
                "metadata_valid=" + fileValid.getAbsolutePath(), //
                "metadata_responses=" + fileResponses.getAbsolutePath());
        args.addAll(debuggingOutputLocations);
        LOG.info("Running {} with {} arguments (configure output details in log4j.properties)",
                flux, args.size());
        LOG.debug("Full arguments: {}", maskCredentials(args));
        return args;
    }

    private static void logSummary(File flux, File failProcess, File invalid, File responses,
            long end) throws IOException {
        String countFailProcess = "0";
        String notAvailable = "n/a";
        String countInvalid = notAvailable;
        String countSuccess = notAvailable;
        String countFailWrite = notAvailable;
        if (failProcess.exists()) {
            try (Stream<String> errored = Files.lines(failProcess.toPath())) {
                countFailProcess = errored.count() + "";
            }
        }
        if (invalid.exists()) {
            try (Stream<String> invalids = Files.lines(invalid.toPath())) {
                countInvalid = invalids.count() + "";
            }
        }
        if (responses.exists()) {
            try (Stream<String> allResponses = Files.lines(responses.toPath())) {
                ImmutablePair<Integer, Integer> successAndFailed = allResponses.map(JSONObject::new)
                        .map(r -> ImmutablePair.of(r.getInt("success"), r.getInt("failed")))
                        .reduce(ImmutablePair.of(0, 0), (all, current) -> ImmutablePair
                                .of(all.left + current.left, all.right + current.right));
                countSuccess = String.valueOf(successAndFailed.left);
                countFailWrite = String.valueOf(successAndFailed.right);
            }
        }

        String errorDetails = String.format("FAIL-PROCESS: %s, FAIL-VALIDATION: %s, FAIL-WRITE: %s",
                countFailProcess, countInvalid, countFailWrite);
        if (notAvailable.equals(countSuccess) || Integer.valueOf(countSuccess) == 0) {
            throw new IllegalStateException(
                    String.format("No data fully processed: %s, %s", countSuccess, errorDetails));
        }
        LOG.info("Import channel {}, SUCCESS: {}, {}, DURATION: {}", flux.getName(), countSuccess,
                errorDetails, formatTime(end));
    }

    static Object[] maskCredentials(List<String> args) {
        return new Object[] {
                args.stream().map(arg -> arg.replaceAll("_(user|pass|auth)=.*", "_$1=<masked>"))
                        .collect(Collectors.toList()) };
    }

    private static void writeTestOutput(File fileOrDir) throws IOException {
        if (!new File(OUT_FILE.getParent()).exists()) {
            return;
        }
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
        LOG.info("Writing {} to {}", json, OUT_FILE);
        String bulk = Files.readAllLines(Paths.get(json.toURI())).stream()
                .collect(Collectors.joining("\n"));
        w.write(bulk);
        w.write("\n");
    }
}
