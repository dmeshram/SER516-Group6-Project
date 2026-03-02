package edu.asu.ser516.metrics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvMetricExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void createsCsvFileInOutputDirWithCorrectFilename() throws Exception {
        CsvMetricExporter exporter = new CsvMetricExporter();
        exporter.export(List.of(), tempDir);

        Path outFile = tempDir.resolve("fanout.csv");
        assertTrue(Files.exists(outFile), "Expected CSV file to be created: " + outFile);
    }

    @Test
    void writesHeaderAsFirstLine() throws Exception {
        CsvMetricExporter exporter = new CsvMetricExporter();
        exporter.export(List.of(), tempDir);

        Path outFile = tempDir.resolve("fanout.csv");
        String firstLine = Files.readAllLines(outFile).get(0);

        assertEquals("metricType,scope,entity,value,packageName,filePath", firstLine);
    }

    @Test
    void writesOneRowCorrectly() throws Exception {
        CsvMetricExporter exporter = new CsvMetricExporter();

        MetricRow row = new MetricRow(
                MetricType.FAN_OUT,
                Scope.CLASS,
                "simplejavacalculator.UI",
                13,
                "simplejavacalculator",
                "UI.java"
        );

        exporter.export(List.of(row), tempDir);

        Path outFile = tempDir.resolve("fanout.csv");
        List<String> lines = Files.readAllLines(outFile);

        assertEquals(2, lines.size(), "Expected header + 1 data row");
        assertEquals("FAN_OUT,CLASS,simplejavacalculator.UI,13,simplejavacalculator,UI.java", lines.get(1));
    }
}