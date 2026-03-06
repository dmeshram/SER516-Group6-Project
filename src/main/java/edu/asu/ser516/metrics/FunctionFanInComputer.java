package edu.asu.ser516.metrics;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FunctionFanInComputer {

    public Map<String, Integer> compute(Path root) {

        try {
            List<Path> javaFiles = Files.walk(root)
                    .filter(p -> p.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            MethodCouplingAnalyzer analyzer = new MethodCouplingAnalyzer(javaFiles);

            analyzer.analyze();

            Map<String, Set<String>> callerToHeuristicCalls = analyzer.getMethodAdjacencyList();

            Set<String> projectMethods = analyzer.getProjectMethods();

            Map<String, Set<String>> calleeToCallers = new HashMap<>();

            // Reverse graph using heuristic matching
            for (Map.Entry<String, Set<String>> entry : callerToHeuristicCalls.entrySet()) {

                String caller = entry.getKey();

                for (String heuristicCall : entry.getValue()) {

                    String methodName = heuristicCall.substring(0, heuristicCall.indexOf("("));

                    int argCount = Integer.parseInt(
                            heuristicCall.substring(
                                    heuristicCall.indexOf("(") + 1,
                                    heuristicCall.indexOf(" args")).trim());

                    for (String projectMethod : projectMethods) {

                        if (projectMethod.contains("." + methodName + "(")) {

                            String params = projectMethod.substring(
                                    projectMethod.indexOf("(") + 1,
                                    projectMethod.indexOf(")"));

                            int declaredCount = params.isEmpty() ? 0 : params.split(",").length;

                            if (declaredCount == argCount &&
                                    !projectMethod.equals(caller)) {

                                calleeToCallers
                                        .computeIfAbsent(projectMethod, k -> new HashSet<>())
                                        .add(caller);
                            }
                        }
                    }
                }
            }

            Map<String, Integer> result = new HashMap<>();

            for (String method : projectMethods) {
                result.put(method,
                        calleeToCallers.getOrDefault(method, Set.of()).size());
            }

            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}