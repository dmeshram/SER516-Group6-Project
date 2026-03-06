package edu.asu.ser516.metrics;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FanInOverloadedMethodsTest {

    @Test
    void fanInIsComputedSeparatelyForOverloads() {
        Path input = Path.of("src/test/resources/fixtures/fanin_overload");

        Map<String, Integer> fanInByMethod = new FunctionFanInComputer().compute(input);

        // Canonical IDs per spec
        String fInt = "fixtures.fanin_overload.Over#f(int)";
        String fStr = "fixtures.fanin_overload.Over#f(java.lang.String)";

        assertEquals(1, fanInByMethod.getOrDefault(fInt, 0),
                "Expected Over#f(int) to have fan-in 1");
        assertEquals(1, fanInByMethod.getOrDefault(fStr, 0),
                "Expected Over#f(String) to have fan-in 1");
    }
}