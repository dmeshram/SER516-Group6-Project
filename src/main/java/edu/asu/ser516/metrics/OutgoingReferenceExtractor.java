package edu.asu.ser516.metrics;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class OutgoingReferenceExtractor {
    private static final class FileContext {
        final String pkg;
        final Set<String> importsExact;
        final Set<String> importsWildcard;
        FileContext(String pkg, Set<String> importsExact, Set<String> importsWildcard) {
            this.pkg = pkg;
            this.importsExact = importsExact;
            this.importsWildcard = importsWildcard;
        }
    }
    public static Map<String, Set<String>> extractOutgoingRefs(List<Path> javaFiles, Set<String> projectClasses) {
        Map<String, Set<String>> outgoing = new HashMap<>();
        JavaParser parser = new JavaParser();
        for (Path file : javaFiles) {
            CompilationUnit cu;
            try {
                ParseResult<CompilationUnit> result = parser.parse(file);
                if (result.getResult().isEmpty()) {
                    System.out.println("WARN: Could not parse " + file);
                    continue;
                }
                cu = result.getResult().get();
            } catch (IOException ex) {
                System.out.println("WARN: IO error reading " + file + " -> " + ex.getMessage());
                continue;
            }
            FileContext ctx = buildContext(cu);
            for (TypeDeclaration<?> td : cu.getTypes()) {
                if (!(td instanceof ClassOrInterfaceDeclaration)) continue;
                String className = td.getNameAsString();
                String currentFqcn = ctx.pkg.isEmpty() ? className : ctx.pkg + "." + className;
                Set<String> deps = outgoing.computeIfAbsent(currentFqcn, k -> new HashSet<>());
                td.findAll(ObjectCreationExpr.class).forEach(expr -> {
                    String simple = expr.getType().getNameAsString();
                    addIfProject(resolve(simple, ctx), currentFqcn, deps, projectClasses);
                });
                td.findAll(ClassOrInterfaceType.class).forEach(t -> {
                    String simple = t.getNameAsString();
                    addIfProject(resolve(simple, ctx), currentFqcn, deps, projectClasses);
                });
                td.findAll(MethodCallExpr.class).forEach(call -> {
                    call.getScope().ifPresent(scope -> {
                        if (scope.isNameExpr()) {
                            String simple = scope.asNameExpr().getNameAsString();
                            addIfProject(resolve(simple, ctx), currentFqcn, deps, projectClasses);
                        }
                    });
                });
            }
        }
        return outgoing;
    }
    private static FileContext buildContext(CompilationUnit cu) {
        String pkg = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
        Set<String> exact = new HashSet<>();
        Set<String> wild = new HashSet<>();
        for (ImportDeclaration id : cu.getImports()) {
            if (id.isAsterisk()) {
                wild.add(id.getNameAsString());
            } else {
                exact.add(id.getNameAsString());
            }
        }
        return new FileContext(pkg, exact, wild);
    }
    private static String resolve(String simpleName, FileContext ctx) {
        if (simpleName == null || simpleName.isBlank()) return null;
        Set<String> common = Set.of(
                "String","Integer","Long","Double","Float","Boolean","Object",
                "List","Map","Set","Optional"
        );
        if (common.contains(simpleName)) return null;
        for (String imp : ctx.importsExact) {
            if (imp.endsWith("." + simpleName)) return imp;
        }
        for (String basePkg : ctx.importsWildcard) {
            return basePkg + "." + simpleName;
        }
        if (!ctx.pkg.isEmpty()) return ctx.pkg + "." + simpleName;
        return simpleName; 
    }
    private static void addIfProject(String candidateFqcn,
                                    String currentFqcn,
                                    Set<String> deps,
                                    Set<String> projectClasses) {
        if (candidateFqcn == null) return;
        if (candidateFqcn.equals(currentFqcn)) return; 
        if (projectClasses.contains(candidateFqcn)) deps.add(candidateFqcn);
    }
}
