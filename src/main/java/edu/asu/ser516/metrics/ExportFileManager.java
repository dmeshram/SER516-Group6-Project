package edu.asu.ser516.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportFileManager {

    public static Path prepareOutputFile(Path outputDir, String metricName, String extension) throws IOException {

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        String fileName = metricName.toLowerCase()
                + "_" + System.currentTimeMillis()
                + "." + extension;

        return outputDir.resolve(fileName);
    }
}