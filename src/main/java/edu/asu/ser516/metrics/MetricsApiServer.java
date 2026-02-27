package edu.asu.ser516.metrics;

import io.javalin.Javalin;
import io.javalin.http.Context;

public final class MetricsApiServer {
    private MetricsApiServer() {}

    public static Javalin create() {
        return Javalin.create()
                .get("/metrics/fanout", MetricsApiServer::handleFanOut);
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        create().start(port);
    }

    private static void handleFanOut(Context ctx) {
        // TODO: implement in Future Task
    }
}
