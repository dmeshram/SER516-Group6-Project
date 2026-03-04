package edu.asu.ser516.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CsvMetricExporter {

    private static final String FILE_NAME = "fanout.csv";
    private static final String HEADER = "metricType,scope,entity,value,packageName,filePath";

    public void export(List<MetricRow> rows, Path outDir) throws IOException {
        Objects.requireNonNull(outDir, "outDir");

        Files.createDirectories(outDir);
        Path outFile = outDir.resolve(FILE_NAME);

        List<String> lines = new ArrayList<>();
        lines.add(HEADER);

        List<MetricRow> safeRows = (rows == null) ? Collections.emptyList() : rows;
        List<MetricRow> sorted = MetricRowSorter.sort(safeRows);

        for (MetricRow r : sorted) {
            lines.add(toCsvLine(r));
        }

        Files.write(outFile, lines);
    }

    private static String toCsvLine(MetricRow r) {
        Objects.requireNonNull(r, "MetricRow");

        return String.join(",",
                r.getMetricType().name(),
                r.getScope().name(),
                r.getEntity(),
                String.valueOf(r.getValue()),
                nullToEmpty(r.getPackageName()),
                nullToEmpty(r.getFilePath())
        );
    }

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }
}