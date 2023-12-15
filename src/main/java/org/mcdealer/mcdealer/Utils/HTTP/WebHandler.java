package org.mcdealer.mcdealer.Utils.HTTP;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class WebHandler implements HttpHandler {
    private final WebServer webServer;

    public WebHandler(WebServer webServer) {
        this.webServer = webServer;
    }

    // Handler for the web server
    public void handle(HttpExchange exchange) throws IOException {
        // Resource folder path “/web”
        String webFolderPath = "web";

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