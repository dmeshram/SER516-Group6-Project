package edu.asu.ser516.metrics;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

class JsonMetricExporterTest {

    @Test
    void shouldCreateJsonFileInOutputDirectory() throws Exception {
        // Arrange
        Path tempDir = Files.createTempDirectory("json-metrics-test");

        List<MetricRow> rows = List.of(
                new MetricRow(
                        MetricType.FAN_OUT,
                        Scope.CLASS,
                        "edu.asu.Calculator",
                        "edu.asu",
                        "src/main/java/edu/asu/Calculator.java",
                        3
                )
        );

        JsonMetricExporter exporter = new JsonMetricExporter();

        // Act
        Path outputFile = exporter.export(rows, tempDir);

        // Assert
        assertNotNull(outputFile, "Returned file path should not be null");
        assertTrue(Files.exists(outputFile), "JSON file should be created");
        assertTrue(Files.size(outputFile) > 0, "JSON file should not be empty");
        assertTrue(outputFile.toString().endsWith(".json"), "Output file must have .json extension");
    }
}
@Test
void shouldProduceValidJsonStructure() throws Exception {
    // Arrange
    Path tempDir = Files.createTempDirectory("json-metrics-test");

    List<MetricRow> rows = List.of(
            new MetricRow(
                    MetricType.FAN_OUT,
                    Scope.CLASS,
                    "edu.asu.Calculator",
                    "edu.asu",
                    "src/main/java/edu/asu/Calculator.java",
                    3
            )
    );

    JsonMetricExporter exporter = new JsonMetricExporter();

    // Act
    Path outputFile = exporter.export(rows, tempDir);
    String content = Files.readString(outputFile);

    // Assert
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(content);

    assertNotNull(root);

    // Top-level fields
    assertTrue(root.has("metric"), "JSON must contain 'metric'");
    assertTrue(root.has("scope"), "JSON must contain 'scope'");
    assertTrue(root.has("results"), "JSON must contain 'results'");

    // results must be array
    assertTrue(root.get("results").isArray(), "'results' must be an array");

    JsonNode firstResult = root.get("results").get(0);

    assertTrue(firstResult.has("entity"), "Result must contain 'entity'");
    assertTrue(firstResult.has("package"), "Result must contain 'package'");
    assertTrue(firstResult.has("file"), "Result must contain 'file'");
    assertTrue(firstResult.has("value"), "Result must contain 'value'");

    assertEquals(3, firstResult.get("value").asInt());
}
@Test
void shouldWriteEmptyResultsArrayWhenNoMetrics() throws Exception {
    // Arrange
    Path tempDir = Files.createTempDirectory("json-metrics-test");

    JsonMetricExporter exporter = new JsonMetricExporter();

    // Act
    Path outputFile = exporter.export(List.of(), tempDir);
    String content = Files.readString(outputFile);

    // Assert
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(content);

    assertNotNull(root);
    assertTrue(root.has("results"), "JSON must contain 'results'");
    assertTrue(root.get("results").isArray(), "'results' must be an array");
    assertEquals(0, root.get("results").size(), "Results array must be empty");
}