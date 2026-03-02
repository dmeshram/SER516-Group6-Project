package edu.asu.ser516.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvMetricExporter {

    private static final String FILE_NAME = "fanout.csv";

    public void export(List<MetricRow> rows, Path outDir) throws IOException {
        Files.createDirectories(outDir);

        Path outFile = outDir.resolve(FILE_NAME);

        if (!Files.exists(outFile)) {
            Files.createFile(outFile);
        }
    }
}