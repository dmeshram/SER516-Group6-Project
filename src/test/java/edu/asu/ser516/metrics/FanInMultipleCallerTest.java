package edu.asu.ser516.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FanInMultipleCallerTest {
    private static final Path FIXTURE =
            Path.of("src/test/resources/fixtures/fanin_multiple_callers");

    @Test
    @DisplayName("Method called by two distinct callers has fanIn = 2")
    void fanInWhenTwoCallersInvokeTargetMethod() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertTrue(
                fanInByMethod.containsKey("fixtures.fanin_multiple_callers.Target#compute()"),
                "Target#compute() must appear in the result"
        );
        assertTrue(
                fanInByMethod.get("fixtures.fanin_multiple_callers.Target#compute()") >= 2,
                "Target#compute() must have fanIn >= 2 — called by at least two distinct callers"
        );
    }

    @Test
    @DisplayName("Method called by three distinct callers has fanIn = 3")
    void fanInWhenThreeCallersInvokeTargetMethod() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertEquals(
                3,
                fanInByMethod.get("fixtures.fanin_multiple_callers.Target#compute()"),
                "Target#compute() is called by CallerOne, CallerTwo and CallerThree — fanIn must be 3"
        );
    }

    @Test
    @DisplayName("Caller methods themselves have fanIn = 0 (nobody calls them)")
    void callerMethodsHaveFanInZero() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertEquals(0,
                fanInByMethod.get("fixtures.fanin_multiple_callers.CallerOne#run()"),
                "CallerOne#run() is never called — must have fanIn = 0");
        assertEquals(0,
                fanInByMethod.get("fixtures.fanin_multiple_callers.CallerTwo#execute()"),
                "CallerTwo#execute() is never called — must have fanIn = 0");
        assertEquals(0,
                fanInByMethod.get("fixtures.fanin_multiple_callers.CallerThree#process()"),
                "CallerThree#process() is never called — must have fanIn = 0");
    }

    @Test
    @DisplayName("Result contains entries for all declared methods")
    void resultContainsAllDeclaredMethods() {
        Map<String, Integer> fanInByMethod =
                new FunctionFanInComputer().compute(FIXTURE);

        assertEquals(4, fanInByMethod.size(),
                "All 4 declared methods must appear in the result");
    }
}
