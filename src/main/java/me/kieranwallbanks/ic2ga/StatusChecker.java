package me.kieranwallbanks.ic2ga;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import me.kieranwallbanks.ic2ga.config.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class StatusChecker {
    private InetSocketAddress address;
    private HttpServer server;
    private HttpContext context;

    private static final byte[] RESPONSE = "All good!".getBytes();

    public StatusChecker(Status status) {
        try {
            address = new InetSocketAddress(status.getPort());
            server = HttpServer.create(address, 0);
            server.setExecutor(null);

            context = server.createContext("/", this::handleRequest);

            Runtime.getRuntime().addShutdownHook(new Thread(this::close));

            System.out.println("Created status checker on " + address.toString() + "...");
        } catch (IOException e) {
            System.out.println("ERROR: can't start the status checker!");
            e.printStackTrace();
        }
    }

    public void start() {
        server.start();
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        System.out.println("Someone requested a status check...");

        exchange.sendResponseHeaders(200, RESPONSE.length);
        exchange.getResponseHeaders().add("Content-Type", "text/plain");

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(RESPONSE);
        outputStream.close();

        System.out.println("Status check response sent...");
    }

    public void close() {
        System.out.println("\nCleaning up status checker...");
        server.stop(0);
    }
}
