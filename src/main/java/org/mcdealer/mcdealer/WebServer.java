package org.mcdealer.mcdealer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.logging.Level;

public class WebServer extends MCDealer {

    private HttpServer server;
    private final MCDealer plugin;

    public WebServer(MCDealer plugin) {
        this.plugin = plugin;
    }

    public void RunWebServer() {
        // Load the configuration from config.yml
        plugin.saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Port on which the web server listens (default value: 8080)
        int port = config.getInt("web-server-port", 8080);

        try {
            // Create the web server and bind it to the specified address and port
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Create the context for the /web path and set the handler
            server.createContext("/web", new WebHandler(this));

            // Start the web server
            server.start();

            getLogger().info(" MCDealer: Webserver started at port " + port);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "An error occurred while starting the web server", e);
        }
    }

    public void stopWebServer() {
        if (server != null) {
            server.stop(0);  // Stop the server with a delay of 0 seconds
            getLogger().info("MCDealer: Webserver stopped");
        }
    }

    public void restartWebServer() {
        stopWebServer();
        RunWebServer();
    }

    static class WebHandler implements HttpHandler {
        private final WebServer webServer;

        public WebHandler(WebServer webServer) {
            this.webServer = webServer;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            webServer.handle(exchange);
        }
    }

    // Handler for the web server
    public void handle(HttpExchange exchange) throws IOException {
        // Resource folder path “/web”
        String webFolderPath = "./plugins/MCDealer/web";

        //Extract the path from the request
        String path = exchange.getRequestURI().getPath();
        File file = new File(webFolderPath + path);

        // Check if the file exists and is a regular file
        if (file.exists() && file.isFile()) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Set the status code and send the file as a response
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        } else {
            //file not found
            String response = "Data not found.";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

    }
}
