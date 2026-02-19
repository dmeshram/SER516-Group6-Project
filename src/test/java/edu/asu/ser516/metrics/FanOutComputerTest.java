package edu.asu.ser516.metrics;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FanOutComputerTest {

    @Test
    void computesDistinctFanOutPerClass() {
        List<ClassReference> refs = List.of(
                new ClassReference("A", "B"),
                new ClassReference("A", "C"),
                new ClassReference("A", "B")  // duplicate
        );

        Map<String, Integer> fanOut = FanOutComputer.computeFanOut(refs);

        assertEquals(1, fanOut.size());
        assertEquals(2, fanOut.get("A"));  // B and C, distinct
    }

    @Test
    void excludesSelfReferences() {
        List<ClassReference> refs = List.of(
                new ClassReference("A", "A"),
                new ClassReference("A", "B")
        );

        Map<String, Integer> fanOut = FanOutComputer.computeFanOut(refs);

        assertEquals(1, fanOut.get("A"));  // only B
    }

    @Test
    void emptyInputReturnsEmptyMap() {
        Map<String, Integer> fanOut = FanOutComputer.computeFanOut(List.of());

        assertTrue(fanOut.isEmpty());
    }

    @Test
    void multipleClassesWithDifferentFanOut() {
        List<ClassReference> refs = List.of(
                new ClassReference("Service", "Repository"),
                new ClassReference("Service", "Model"),
                new ClassReference("Service", "Util"),
                new ClassReference("Controller", "Service"),
                new ClassReference("Controller", "Model")
        );

        Map<String, Integer> fanOut = FanOutComputer.computeFanOut(refs);

        assertEquals(3, fanOut.get("Service"));
        assertEquals(2, fanOut.get("Controller"));
    }

    @Test
    void classWithNoOutgoingReferencesNotInResult() {
        List<ClassReference> refs = List.of(
                new ClassReference("A", "B")
        );

        Map<String, Integer> fanOut = FanOutComputer.computeFanOut(refs);

        assertTrue(fanOut.containsKey("A"));
        assertFalse(fanOut.containsKey("B"));  // B has no outgoing refs
    }
}
