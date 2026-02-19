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
        System.out.println("Found .java files: " + files.size());
        files.stream().limit(10).forEach(p -> System.out.println(" - " + p));
    }
}
