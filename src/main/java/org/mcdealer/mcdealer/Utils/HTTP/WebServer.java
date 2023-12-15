package org.mcdealer.mcdealer.Utils.HTTP;

import org.mcdealer.mcdealer.MCDealer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer extends MCDealer {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    private HttpServer server;
    private final MCDealer plugin;

    public WebServer(MCDealer plugin) {
        this.plugin = plugin;
    }

    public void runWebServer() {
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

            logger.info("Webserver started at port " + port);
        } catch (IOException e) {
            logger.error(" An error occurred while starting the web server", e);
        }
    }

    public void stopWebServer() {
        if (server != null) {
            server.stop(0);  // Stop the server with a delay of 0 seconds
            logger.info("Webserver stopped");
        }
    }
}