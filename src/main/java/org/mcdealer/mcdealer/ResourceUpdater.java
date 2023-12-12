package org.mcdealer.mcdealer;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Level;

public class ResourceUpdater extends MCDealer {

    private final MCDealer plugin;

    public ResourceUpdater(MCDealer plugin) {
        this.plugin = plugin;
    }

    void updateConfig() {
        //Loading the current configuration file
        int currentConfigVersion = getConfig().getInt("configversion", 0);
        int newConfigVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("configversion", 0);

        if (newConfigVersion > currentConfigVersion) {
            // Version difference, update configuration file
            this.getLogger().info(" MCDealer: updating config.yml... ");
            plugin.saveResource("config.yml", true);
        }
    }

    void updateWebFolder() {
        // Check and update the "web" folder
        File webFolder = new File(getDataFolder(), "web");

        int currentWebFilesVersion = getConfig().getInt("webfilesversion", 0);
        int newWebFilesVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("webfilesversion", 0);

        if (newWebFilesVersion > currentWebFilesVersion) {
            // Version difference, update contents of the "web" folder
            this.getLogger().info(" MCDealer: updating web files... ");
            updateFolderFromJar(webFolder);
        }
    }

    void updatePyScript() {
        //Loading the current configuration file
        int currentPyScriptVersion = getConfig().getInt("pyscriptversion", 0);
        int newPyScriptVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("pyscriptversion", 0);

        if (newPyScriptVersion > currentPyScriptVersion) {
            // Version difference, update configuration file
            this.getLogger().info(" MCDealer: updating script... ");
            plugin.saveResource("data-yml2json.py", true);
        }
    }

    private File getConfigFromJar() {
        // Loading the "config.yml" from the JAR file
        InputStream resource = getResource("config.yml");

        // Check if the resource is null
        if (resource == null) {
            plugin.getLogger().severe("Unable to find config.yml in the JAR file.");
            return null;
        }

        // Save the "config.yml" from the JAR file in a temporary directory
        File tempConfigFile;
        try {
            tempConfigFile = File.createTempFile("tempConfig", ".yml");
            Files.copy(resource, tempConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            resource.close();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "An error occurred while copying config.yml from JAR to temporary directory", e);
            return null;
        }

        // Load the "config.yml" from the temporary directory
        return tempConfigFile;
    }

    private void updateFolderFromJar(File destinationFolder) {
        // Load the resources from the JAR folder "web" and copy them to the target directory
        File jarFolder = new File(getDataFolder(), "web");

        if (jarFolder.exists() && jarFolder.isDirectory()) {
            // Delete the existing target directory
            deleteFolder(destinationFolder);
        }
        if (!destinationFolder.mkdirs()) {
            // Handle the case where directory creation failed
            System.err.println("Failed to create the target directory: " + destinationFolder.getAbsolutePath());
        } else {
            // Directory created successfully, proceed with copying resources
            plugin.saveResource("web" + "/", false);
        }
    }

    private void deleteFolder(File folder) {
        // Deleting a directory and all of its subdirectories and files
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        if (!file.delete()) {
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }

            if (!folder.delete()) {
                System.err.println("Failed to delete folder: " + folder.getAbsolutePath());
            }
        }
    }

}