package edu.asu.ser516.metrics;

import java.nio.file.Path;
import java.util.*;

public class MetricPipelineMain {

    public static void main(String[] args) throws Exception {

        String inputPathStr = (args.length >= 1) ? args[0]
                : System.getenv("INPUT_PATH");
        String outDirStr = (args.length >= 2) ? args[1]
                : System.getenv().getOrDefault("OUTPUT_DIR", "metrics-output");

        if (inputPathStr == null || inputPathStr.isBlank()) {
            System.err.println("ERROR: inputPath is required. " +
                    "Pass as first argument or set INPUT_PATH env var.");
            System.exit(1);
        }

        Path inputPath = Path.of(inputPathStr);
        Path outDir   = Path.of(outDirStr);

        if (!inputPath.toFile().exists()) {
            System.err.println("ERROR: inputPath does not exist: " + inputPath);
            System.exit(1);
        }

        System.out.println("Scanning: " + inputPath.toAbsolutePath());

        List<Path> javaFiles = SourceScanner.findJavaFiles(inputPath);
        System.out.println("Found " + javaFiles.size() + " Java files.");

        Map<String, Set<String>> outgoing =
                OutgoingReferenceExtractor.extractOutgoingRefs(javaFiles);
        List<ClassReference> edges = ReferenceAdapters.toEdges(outgoing);
        Map<String, Integer> fanOut = FanOutComputer.computeFanOut(edges);

        List<MetricRow> rows = new ArrayList<>();
        fanOut.forEach((entity, value) ->
                rows.add(new MetricRow(MetricType.FAN_OUT, Scope.CLASS,
                        entity, value, extractPackage(entity), null)));

        CsvMetricExporter  csvExporter  = new CsvMetricExporter();
        JsonMetricExporter jsonExporter = new JsonMetricExporter();

        Path csvFile  = csvExporter.export(rows, outDir);
        Path jsonFile = jsonExporter.export(rows, outDir);

        System.out.println("CSV  written: " + csvFile.toAbsolutePath());
        System.out.println("JSON written: " + jsonFile.toAbsolutePath());
    }

    private static String extractPackage(String fqcn) {
        int lastDot = fqcn.lastIndexOf('.');
        return (lastDot > 0) ? fqcn.substring(0, lastDot) : "";
    }
}