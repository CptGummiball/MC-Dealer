package org.mcdealer.mcdealer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class ResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger("MC Dealer");

    public static void copyResources(String sourcePath, String destinationPath) {
        try {
            // Get a list of resources with the specified name/pattern
            InputStream resourceListStream = ResourceUtils.class.getClassLoader().getResourceAsStream(sourcePath);
            if (resourceListStream != null) {
                String[] resourceNames = new String[0];
                try (Scanner scanner = new Scanner(resourceListStream, StandardCharsets.UTF_8)) {
                    String resourceListContent = scanner.useDelimiter("\\A").next();
                    if (!resourceListContent.trim().isEmpty()) {
                        resourceNames = resourceListContent.split("\n");
                    } else {
                        logger.warn("Resource list is empty");
                    }
                }

                // Copy each resource to the destination path
                for (String resourceName : resourceNames) {
                    if (resourceName.trim().isEmpty()) {
                        continue; // Skip empty lines
                    }

                    try (InputStream resourceStream = ResourceUtils.class.getClassLoader().getResourceAsStream(resourceName)) {
                        if (resourceStream != null) {
                            Path destination = Path.of(destinationPath, resourceName);
                            Files.createDirectories(destination.getParent());
                            Files.copy(resourceStream, destination, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            logger.warn("Resource not found: {}", resourceName);
                        }
                    }
                }
            } else {
                logger.error("Resource list not found: {}", sourcePath);
            }
        } catch (IOException e) {
            logger.error("Error copying resources", e);
        }
    }
}
