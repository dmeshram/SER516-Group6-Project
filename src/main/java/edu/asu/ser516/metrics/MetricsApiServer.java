package edu.asu.ser516.metrics;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class MetricsApiServer {

    private MetricsApiServer() {}
    
    public static Javalin create() {
        return Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
        })
        .get("/metrics/fanout", MetricsApiServer::handleFanOut)
        .get("/metrics/fanin",  MetricsApiServer::handleFanIn);
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        create().start(port);
        System.out.println("Metrics API server started on port " + port);
    }

    private static void handleFanOut(Context ctx) {
        Path root;
        try {
            root = validatePath(ctx);
        } catch (IllegalArgumentException e) {
            sendError(ctx, e.getMessage());
            return;
        }

        try {
            List<Path> javaFiles = SourceScanner.findJavaFiles(root);
            Map<String, Set<String>> outgoing = OutgoingReferenceExtractor.extractOutgoingRefs(javaFiles);
            List<ClassReference> edges = ReferenceAdapters.toEdges(outgoing);
            Map<String, Integer> fanOut = FanOutComputer.computeFanOut(edges);

            Map<String, Integer> sorted = fanOut.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            ctx.json(toFanOutJsonArray(sorted));

        } catch (IOException e) {
            sendError(ctx, "Failed to scan project at path: " + root + " — " + e.getMessage());
        }
    }

    private static void handleFanIn(Context ctx) {
        Path root;
        try {
            root = validatePath(ctx);
        } catch (IllegalArgumentException e) {
            sendError(ctx, e.getMessage());
            return;
        }

        try {
            List<Path> javaFiles = SourceScanner.findJavaFiles(root);

            CouplingAnalyzer analyzer = new CouplingAnalyzer(javaFiles);
            analyzer.analyze();
            Map<String, Integer> fanIn = analyzer.getFanIn();

            Map<String, Integer> sorted = fanIn.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            ctx.json(toFanInJsonArray(sorted));

        } catch (IOException e) {
            sendError(ctx, "Failed to scan project at path: " + root + " — " + e.getMessage());
        }
    }

    private static Path validatePath(Context ctx) {
        String pathParam = ctx.queryParam("path");

        if (pathParam == null || pathParam.isBlank()) {
            ctx.status(400);
            throw new IllegalArgumentException(
                "Required query parameter 'path' is missing or empty. " +
                "Usage: /metrics/fanout?path=/absolute/path/to/java/project"
            );
        }

        Path root = Path.of(pathParam);

        if (!Files.exists(root)) {
            ctx.status(400);
            throw new IllegalArgumentException(
                "Path does not exist: " + pathParam
            );
        }

        if (!Files.isDirectory(root)) {
            ctx.status(400);
            throw new IllegalArgumentException(
                "Path is not a directory: " + pathParam
            );
        }

        return root;
    }

    private static void sendError(Context ctx, String message) {
        ctx.status(400);
        ctx.contentType("application/json");
        ctx.result("{\"error\":\"" + jsonEscape(message) + "\"}");
    }

    private static String toFanOutJsonArray(Map<String, Integer> fanOut) {
        StringBuilder sb = new StringBuilder("[\n");
        int i = 0;
        int n = fanOut.size();
        for (Map.Entry<String, Integer> e : fanOut.entrySet()) {
            sb.append("  {\"class\":\"")
              .append(jsonEscape(e.getKey()))
              .append("\",\"fanOut\":")
              .append(e.getValue())
              .append("}");
            if (++i < n) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toFanInJsonArray(Map<String, Integer> fanIn) {
        StringBuilder sb = new StringBuilder("[\n");
        int i = 0;
        int n = fanIn.size();
        for (Map.Entry<String, Integer> e : fanIn.entrySet()) {
            sb.append("  {\"class\":\"")
              .append(jsonEscape(e.getKey()))
              .append("\",\"fanIn\":")
              .append(e.getValue())
              .append("}");
            if (++i < n) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}