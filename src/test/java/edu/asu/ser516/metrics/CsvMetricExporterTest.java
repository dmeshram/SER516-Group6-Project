package edu.asu.ser516.metrics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
}