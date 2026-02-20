package edu.asu.ser516.metrics;

import java.nio.file.Path;
import java.util.List;

public class ScannerMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: ScannerMain <path-to-jave-project>");
            System.exit(1);
        }
        Path root = Path.of(args[0]);
        List<java.nio.file.Path> files = SourceScanner.findJavaFiles(root);
        
        CouplingAnalyzer analyzer = new CouplingAnalyzer(files);
        analyzer.analyze();
        
        var fanOut = analyzer.getFanOut();
        var fanIn = analyzer.getFanIn();
        
        System.out.println("--- Coupling Metrics ---");
        fanOut.keySet().stream().sorted().forEach(className -> {
            int out = fanOut.getOrDefault(className, 0);
            int in = fanIn.getOrDefault(className, 0);
            System.out.printf("Class: %s, Fan-Out: %d, Fan-In: %d%n", className, out, in);
        });
    }
}
