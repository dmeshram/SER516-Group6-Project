package edu.asu.ser516.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonMetricExporter {

    private final ObjectMapper mapper = new ObjectMapper();

    public Path export(List<MetricRow> rows, Path outputDir) throws IOException {

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        String fileName = "metrics_" + System.currentTimeMillis() + ".json";
        Path outputFile = outputDir.resolve(fileName);

        ObjectNode root = mapper.createObjectNode();

        if (!rows.isEmpty()) {
            root.put("metric", rows.get(0).getMetricType().name());
            root.put("scope", rows.get(0).getScope().name());
        } else {
            root.put("metric", "unknown");
            root.put("scope", "unknown");
        }

        ArrayNode resultsArray = mapper.createArrayNode();

        for (MetricRow row : rows) {
            ObjectNode node = mapper.createObjectNode();
            node.put("entity", row.getEntity());
            node.put("package", row.getPackageName());
            node.put("file", row.getFilePath());
            node.put("value", row.getValue());
            resultsArray.add(node);
        }

        root.set("results", resultsArray);

        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(outputFile.toFile(), root);

        return outputFile;
    }
}