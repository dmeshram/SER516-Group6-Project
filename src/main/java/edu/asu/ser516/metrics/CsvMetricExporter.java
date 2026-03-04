package edu.asu.ser516.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvMetricExporter {

    private static final String FILE_NAME = "fanout.csv";
    private static final String HEADER = "metricType,scope,entity,value,packageName,filePath";

    public void export(List<MetricRow> rows, Path outDir) throws IOException {
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve(FILE_NAME);

        List<String> lines = new ArrayList<>();
        lines.add(HEADER);

        List<MetricRow> sorted = MetricRowSorter.sort(rows);

        for (MetricRow r : sorted) {
            lines.add(toCsvLine(r));
        }

        Files.write(outFile, lines);
    }

    private String toCsvLine(MetricRow r) {
        String packageName = r.getPackageName() == null ? "" : r.getPackageName();
        String filePath = r.getFilePath() == null ? "" : r.getFilePath();

        return String.join(",",
                r.getMetricType().name(),
                r.getScope().name(),
                r.getEntity(),
                String.valueOf(r.getValue()),
                packageName,
                filePath
        );
    }
}