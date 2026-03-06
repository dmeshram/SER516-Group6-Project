package edu.asu.ser516.metrics;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MethodCouplingAnalyzerTest {
    private static final Path SAMPLE_PROJECT =
            Paths.get("input", "Simple-Java-Calculator", "src").toAbsolutePath();

    private MethodCouplingAnalyzer analyzeProject(List<Path> files) {
        MethodCouplingAnalyzer analyzer = new MethodCouplingAnalyzer(files);
        analyzer.analyze();
        return analyzer;
    }
}
