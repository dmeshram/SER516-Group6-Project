package edu.asu.ser516.metrics;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FanInRecursionTest {

    @Test
    void recursionDoesNotIncreaseFanIn() {
        Path input = Path.of("src/test/resources/fixtures/fanin_recursion");

        Map<String, Integer> fanInByMethod = new FunctionFanInComputer().compute(input);

        String target = "fixtures.fanin_recursion.R#fact(int)";

        assertEquals(0, fanInByMethod.getOrDefault(target, 0),
                "Self-recursive calls must not count as fan-in");
    }
}   