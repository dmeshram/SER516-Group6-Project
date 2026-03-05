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
        Path tempDir = Files.createTempDirectory("json-metrics-test");

        List<MetricRow> rows = List.of(
                new MetricRow(
                        MetricType.FAN_OUT,
                        Scope.CLASS,
                        "edu.asu.Calculator",
                        "edu.asu",
                        "src/main/java/edu/asu/Calculator.java",
                        3));

        JsonMetricExporter exporter = new JsonMetricExporter();

        Path outputFile = exporter.export(rows, tempDir);

        assertNotNull(outputFile, "Returned file path should not be null");
        assertTrue(Files.exists(outputFile), "JSON file should be created");
        assertTrue(Files.size(outputFile) > 0, "JSON file should not be empty");
        assertTrue(outputFile.toString().endsWith(".json"), "Output file must have .json extension");
    }

    @Test
    void shouldProduceValidJsonStructure() throws Exception {
        Path tempDir = Files.createTempDirectory("json-metrics-test");

        List<MetricRow> rows = List.of(
                new MetricRow(
                        MetricType.FAN_OUT,
                        Scope.CLASS,
                        "edu.asu.Calculator",
                        "edu.asu",
                        "src/main/java/edu/asu/Calculator.java",
                        3));

        JsonMetricExporter exporter = new JsonMetricExporter();

        Path outputFile = exporter.export(rows, tempDir);
        String content = Files.readString(outputFile);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(content);

        assertNotNull(root, "Root JSON node should not be null");

        assertTrue(root.has("metric"), "JSON must contain 'metric'");
        assertTrue(root.has("scope"), "JSON must contain 'scope'");
        assertTrue(root.has("results"), "JSON must contain 'results'");

        assertEquals("FAN_OUT", root.get("metric").asText());
        assertEquals("CLASS", root.get("scope").asText());

        JsonNode results = root.get("results");
        assertTrue(results.isArray(), "'results' must be an array");
        assertEquals(1, results.size(), "Results array must contain one element");

        JsonNode firstResult = results.get(0);

        assertTrue(firstResult.has("entity"), "Result must contain 'entity'");
        assertTrue(firstResult.has("package"), "Result must contain 'package'");
        assertTrue(firstResult.has("file"), "Result must contain 'file'");
        assertTrue(firstResult.has("value"), "Result must contain 'value'");

        assertEquals("edu.asu.Calculator", firstResult.get("entity").asText());
        assertEquals("edu.asu", firstResult.get("package").asText());
        assertEquals("src/main/java/edu/asu/Calculator.java", firstResult.get("file").asText());
        assertEquals(3, firstResult.get("value").asInt());
    }

    @Test
    void shouldWriteEmptyResultsArrayWhenNoMetrics() throws Exception {
        Path tempDir = Files.createTempDirectory("json-metrics-test");
        JsonMetricExporter exporter = new JsonMetricExporter();

        Path outputFile = exporter.export(List.of(), tempDir);
        String content = Files.readString(outputFile);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(content);

        assertNotNull(root, "Root JSON node should not be null");
        assertTrue(root.has("results"), "JSON must contain 'results'");

        JsonNode results = root.get("results");
        assertTrue(results.isArray(), "'results' must be an array");
        assertEquals(0, results.size(), "Results array must be empty");
    }
}