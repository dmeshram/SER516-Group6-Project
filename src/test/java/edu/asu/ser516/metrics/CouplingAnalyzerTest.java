package edu.asu.ser516.metrics;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class CouplingAnalyzerTest {
    private static final Path SAMPLE_PROJECT =
            Paths.get("input", "Simple-Java-Calculator", "src").toAbsolutePath();

    private Map<String, Integer> fanInFor(List<Path> files) {
        CouplingAnalyzer analyzer = new CouplingAnalyzer(files);
        analyzer.analyze();
        return analyzer.getFanIn();
    }
}
