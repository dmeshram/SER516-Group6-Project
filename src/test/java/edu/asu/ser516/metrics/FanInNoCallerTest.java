package edu.asu.ser516.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FanInNoCallersTest {

    private static final Path FIXTURE =
            Path.of("src/test/resources/fixtures/fanin_no_callers");

    @Test
    @DisplayName("Method with no callers appears in result with fanIn = 0")
    void methodWithNoCallersHasFanInZero() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertTrue(
                fanInByMethod.containsKey("fixtures.fanin_no_callers.Alone#neverCalled()"),
                "neverCalled() must appear in the result even with no callers"
        );
        assertEquals(
                0,
                fanInByMethod.get("fixtures.fanin_no_callers.Alone#neverCalled()"),
                "neverCalled() must have fanIn = 0"
        );
    }

    @Test
    @DisplayName("All methods in an isolated class have fanIn = 0")
    void allMethodsInIsolatedClassHaveFanInZero() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        fanInByMethod.forEach((method, fanIn) ->
                assertEquals(0, fanIn,
                        method + " should have fanIn = 0 — nothing in this fixture calls anything")
        );
    }

    @Test
    @DisplayName("Result is non-empty even when no inter-method calls exist")
    void resultIsNonEmptyForIsolatedClass() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertFalse(fanInByMethod.isEmpty(),
                "Declared methods must appear in the result even if never called");
    }

    @Test
    @DisplayName("Exactly two methods tracked for the Alone class")
    void exactlyTwoMethodsTracked() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertEquals(2, fanInByMethod.size(),
                "Alone declares exactly 2 methods — both must appear in result");
    }
}