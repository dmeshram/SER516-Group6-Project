import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {
    @Test
    void ciRuns() {
        assertTrue(true);
    }

    @Test
    void deliberateFailure() {
        fail("Deliberate failure to test pipeline failure handling");
    }
}