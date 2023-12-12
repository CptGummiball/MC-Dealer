package org.mcdealer.mcdealer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ResourceUtils {

    public static void copyResources(String sourcePath, String destinationPath) {
        try {
            // Get a list of resources with the specified name/pattern
            InputStream resourceListStream = ResourceUtils.class.getClassLoader().getResourceAsStream(sourcePath);
            if (resourceListStream != null) {
                String[] resourceNames;
                try (java.util.Scanner scanner = new java.util.Scanner(resourceListStream, StandardCharsets.UTF_8)) {
                    resourceNames = scanner.useDelimiter("\\A").next().split("\n");
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
                            System.err.println("Resource not found: " + resourceName);
                        }
                    }
                }
            } else {
                System.err.println("Resource list not found: " + sourcePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
