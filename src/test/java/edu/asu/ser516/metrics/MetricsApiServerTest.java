package edu.asu.ser516.metrics;

import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MetricsApiServerTest {
    private static final String ProjectPath = Paths.get("input", "Simple-Java-Calculator", "src").toAbsolutePath().toString();

    @Nested
    @DisplayName("GET /metrics/fanout")
    class FanOutPath {

        @Test
        @DisplayName("Returns HTTP 200 for a valid project path")
        void checkForValidPath() {
            JavalinTest.test(MetricsApiServer.create(), (server, client) -> {
                var response = client.get("/metrics/fanout?path=" + ProjectPath);
                assertEquals(200, response.code());
            });
        }

        @Test
        @DisplayName("Response Content-Type is application/json")
        void returnsJsonContentType() {
            JavalinTest.test(MetricsApiServer.create(), (server, client) -> {
                var response = client.get("/metrics/fanout?path=" + ProjectPath);
                assertNotNull(response.header("Content-Type"));
                assertTrue(
                        Objects.requireNonNull(response.header("Content-Type")).contains("application/json"),
                        "Expected Content-Type to contain application/json but got: "
                                + response.header("Content-Type")
                );
            });
        }

        @Test
        @DisplayName("Response body is a non-empty JSON array")
        void returnsNonEmptyJsonArray() {
            JavalinTest.test(MetricsApiServer.create(), (server, client) -> {
                var response = client.get("/metrics/fanout?path=" + ProjectPath);
                assertNotNull(response.body());
                String body = response.body().string();
                assertTrue(body.trim().startsWith("["), "Response should be a JSON array");
                assertTrue(body.trim().length() > 2,   "JSON array should not be empty");
            });
        }
    }
}
