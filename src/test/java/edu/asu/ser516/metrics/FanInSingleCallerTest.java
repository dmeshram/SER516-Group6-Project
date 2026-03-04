package edu.asu.ser516.metrics;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FanInSingleCallerTest {
    @Test
    void fanInIsOneWhenSingleCallerInvokesTargetMethod() {
        Path input = Path.of("src/test/resources/fixtures/fanin_single_caller");
        Map<String, Integer> fanInByMethod = new FunctionFanInComputer().compute(input);
        assertEquals(1, fanInByMethod.get("fixtures.fanin_single_caller.B#target()"));
    }
}
