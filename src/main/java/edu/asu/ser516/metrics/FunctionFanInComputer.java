package edu.asu.ser516.metrics;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Computes method-level Fan-In for a given Java source root.
 *
 * Key format: package.ClassName#methodName(paramType1,paramType2,...)
 * Example: fixtures.fanin_single_caller.B#target()
 *
 * Rules:
 * - Self-calls (recursion) do not count toward Fan-In.
 * - Overloaded methods are tracked separately by their full parameter type
 * list.
 * - Every project method appears in the result, seeded with fanIn = 0.
 * - When multiple overloads share the same arg count, literal-type hints
 * (int/long/double/boolean/char/String literals) are used to pick the
 * best-matching overload. If no hint is available, all same-arity
 * overloads are credited (conservative fall-back).
 */
public class FunctionFanInComputer {

    public Map<String, Integer> compute(Path root) {
        try {
            List<Path> javaFiles = Files.walk(root)
                    .filter(p -> p.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            // ── Pass 1: build the project method registry ──────────────────
            // canonicalKey = "pkg.Class#method(p1,p2,...)"
            // paramTypes = ordered list of declared parameter type strings
            Map<String, List<String>> methodParamTypes = new LinkedHashMap<>();
            // methodName -> list of canonical keys (for fast lookup)
            Map<String, List<String>> nameToKeys = new HashMap<>();

            Map<Path, CompilationUnit> parsedUnits = new LinkedHashMap<>();

            for (Path path : javaFiles) {
                try {
                    CompilationUnit cu = StaticJavaParser.parse(path);
                    parsedUnits.put(path, cu);

                    String pkg = cu.getPackageDeclaration()
                            .map(pd -> pd.getNameAsString()).orElse("");

                    cu.findAll(MethodDeclaration.class).forEach(md -> {
                        Optional<ClassOrInterfaceDeclaration> parent = md
                                .findAncestor(ClassOrInterfaceDeclaration.class);
                        if (parent.isEmpty())
                            return;

                        String className = parent.get().getNameAsString();
                        String fqClass = pkg.isEmpty() ? className : pkg + "." + className;

                        List<String> paramTypes = md.getParameters().stream()
                                .map(Parameter::getTypeAsString)
                                .collect(Collectors.toList());

                        String paramList = String.join(",", paramTypes);
                        String canonicalKey = fqClass + "#" + md.getNameAsString()
                                + "(" + paramList + ")";

                        methodParamTypes.put(canonicalKey, paramTypes);
                        nameToKeys.computeIfAbsent(md.getNameAsString(),
                                k -> new ArrayList<>()).add(canonicalKey);
                    });
                } catch (IOException e) {
                    System.err.println("Failed to parse: " + path);
                }
            }

            // ── Pass 2: build callee -> set-of-distinct-callers map ────────
            Map<String, Set<String>> calleeToCallers = new HashMap<>();

            for (Map.Entry<Path, CompilationUnit> entry : parsedUnits.entrySet()) {
                CompilationUnit cu = entry.getValue();
                String pkg = cu.getPackageDeclaration()
                        .map(pd -> pd.getNameAsString()).orElse("");

                cu.findAll(MethodDeclaration.class).forEach(md -> {
                    Optional<ClassOrInterfaceDeclaration> parent = md.findAncestor(ClassOrInterfaceDeclaration.class);
                    if (parent.isEmpty())
                        return;

                    String className = parent.get().getNameAsString();
                    String fqClass = pkg.isEmpty() ? className : pkg + "." + className;
                    String paramList = md.getParameters().stream()
                            .map(Parameter::getTypeAsString)
                            .collect(Collectors.joining(","));
                    String callerKey = fqClass + "#" + md.getNameAsString()
                            + "(" + paramList + ")";

                    md.findAll(MethodCallExpr.class).forEach(call -> {
                        String calledName = call.getNameAsString();
                        int argCount = call.getArguments().size();

                        List<String> candidates = nameToKeys.getOrDefault(
                                calledName, Collections.emptyList());

                        // Filter to same-arity candidates, excluding self-calls
                        List<String> sameArity = candidates.stream()
                                .filter(c -> !c.equals(callerKey))
                                .filter(c -> methodParamTypes.get(c).size() == argCount)
                                .collect(Collectors.toList());

                        if (sameArity.isEmpty())
                            return;

                        // Single candidate — use it directly
                        if (sameArity.size() == 1) {
                            calleeToCallers
                                    .computeIfAbsent(sameArity.get(0), k -> new HashSet<>())
                                    .add(callerKey);
                            return;
                        }

                        // Multiple overloads with same arity — use literal-type hints
                        List<String> argHints = call.getArguments().stream()
                                .map(FunctionFanInComputer::inferLiteralType)
                                .collect(Collectors.toList());

                        List<String> matched = sameArity.stream()
                                .filter(c -> {
                                    List<String> declared = methodParamTypes.get(c);
                                    for (int i = 0; i < argHints.size(); i++) {
                                        String hint = argHints.get(i);
                                        if (hint == null)
                                            continue;
                                        if (!typeCompatible(hint, declared.get(i)))
                                            return false;
                                    }
                                    return true;
                                })
                                .collect(Collectors.toList());

                        // Use matched; fall back to all same-arity if disambiguation failed
                        List<String> toCredit = matched.isEmpty() ? sameArity : matched;
                        toCredit.forEach(c -> calleeToCallers
                                .computeIfAbsent(c, k -> new HashSet<>())
                                .add(callerKey));
                    });
                });
            }

            // ── Pass 3: seed every project method with 0, then fill counts ──
            Map<String, Integer> result = new LinkedHashMap<>();
            for (String key : methodParamTypes.keySet()) {
                result.put(key,
                        calleeToCallers.getOrDefault(key, Set.of()).size());
            }

            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Infers a simple type hint from a literal expression.
     * Returns null when the argument is not a recognisable literal.
     */
    private static String inferLiteralType(Expression expr) {
        if (expr instanceof IntegerLiteralExpr)
            return "int";
        if (expr instanceof LongLiteralExpr)
            return "long";
        if (expr instanceof DoubleLiteralExpr)
            return "double";
        if (expr instanceof BooleanLiteralExpr)
            return "boolean";
        if (expr instanceof CharLiteralExpr)
            return "char";
        if (expr instanceof StringLiteralExpr)
            return "String";
        if (expr instanceof UnaryExpr ue) {
            if (ue.getOperator() == UnaryExpr.Operator.MINUS
                    && ue.getExpression() instanceof IntegerLiteralExpr) {
                return "int";
            }
        }
        return null;
    }

    /**
     * Returns true if a literal hint type is compatible with a declared param type.
     */
    private static boolean typeCompatible(String hint, String declared) {
        if (hint.equals(declared))
            return true;
        if (hint.equals("String")
                && (declared.equals("String") || declared.equals("java.lang.String")))
            return true;
        if (hint.equals("int")
                && (declared.equals("Integer") || declared.equals("java.lang.Integer")
                        || declared.equals("long") || declared.equals("double")
                        || declared.equals("float")))
            return true;
        return false;
    }
}