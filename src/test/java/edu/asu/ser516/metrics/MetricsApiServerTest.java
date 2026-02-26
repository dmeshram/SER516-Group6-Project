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

        @Test
        @DisplayName("Response array contains 'class' and 'fanOut' keys in each entry")
        void eachEntryHasClassAndFanOutKeys() {
            JavalinTest.test(MetricsApiServer.create(), (server, client) -> {
                var response = client.get("/metrics/fanout?path=" + ProjectPath);
                assertNotNull(response.body());
                String body = response.body().string();
                assertTrue(body.contains("\"class\""),
                        "Each entry must have a 'class' key");
                assertTrue(body.contains("\"fanOut\""),
                        "Each entry must have a 'fanOut' key");
            });
        }

        @Test
        @DisplayName("Response contains UI with fanOut 13")
        void uiClassHasExpectedFanOut() {
            JavalinTest.test(MetricsApiServer.create(), (server, client) -> {
                var response = client.get("/metrics/fanout?path=" + ProjectPath);
                String body = response.body().string();
                assertTrue(
                        body.contains("\"simplejavacalculator.UI\""),
                        "Response must include UI"
                );
                // UI has 13 outgoing dependencies in the sample project
                assertTrue(
                        body.contains("\"fanOut\":13"),
                        "UI must have fanOut of 13"
                );
            });
        }

        @Test
        @DisplayName("Response contains BufferedImageCustom with fanOut 4")
        void bufferedImageCustomHasExpectedFanOut() {
            JavalinTest.test(MetricsApiServer.create(), (server, client) -> {
                var response = client.get("/metrics/fanout?path=" + ProjectPath);
                String body = response.body().string();
                assertTrue(
                        body.contains("\"simplejavacalculator.BufferedImageCustom\""),
                        "Response must include BufferedImageCustom"
                );
                assertTrue(
                        body.contains("\"fanOut\":4"),
                        "simplejavacalculator.BufferedImageCustom must have fanOut of 4"
                );
            });
        }
    }
}
