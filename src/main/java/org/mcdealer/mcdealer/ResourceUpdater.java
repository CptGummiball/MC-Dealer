package org.mcdealer.mcdealer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;

import static org.mcdealer.mcdealer.ResourceUtils.copyResources;

public class ResourceUpdater extends MCDealer {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");

    private final MCDealer plugin;

    public ResourceUpdater(MCDealer plugin) {
        this.plugin = plugin;
    }

    void updateConfig() {

        logger.info("Checking config.yml...");

        //Loading the current configuration file
        int currentConfigVersion = getConfig().getInt("configversion", 0);
        int newConfigVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("configversion", 0);
        logger.info("Current config version: {}", currentConfigVersion);
        logger.info("New config version: {}", newConfigVersion);

        if (newConfigVersion > currentConfigVersion) {
            // Version difference, update configuration file
            logger.info("updating config.yml... ");
            plugin.saveResource("config.yml", true);
        }
    }

    void updateWebFolder() {
        getLogger().info("Checking and updating web folder...");
        int currentWebFilesVersion = getConfig().getInt("webfilesversion", 0);
        int newWebFilesVersion = YamlConfiguration.loadConfiguration(getConfigFromJar()).getInt("webfilesversion", 0);
        logger.info("Current web files version: {}", currentWebFilesVersion);
        logger.info("New web files version: {}", newWebFilesVersion);

        File webFolder = new File(getDataFolder(), "web");
        if (!webFolder.exists() || !webFolder.isDirectory()) {
            getLogger().info("Transferring all web files...");
            extractFolderFromJar("web");
        } else if (newWebFilesVersion > currentWebFilesVersion) {
            getLogger().info("Updating web files... ");
            extractFolderFromJar("web");
            getConfig().set("webfilesversion", newWebFilesVersion);
            saveConfig();
        }
    }

    private File getConfigFromJar() {
        // Loading the "config.yml" from the JAR file
        InputStream resource = getResource("config.yml");

        // Check if the resource is null
        if (resource == null) {
            logger.error("Unable to find config.yml in the JAR file.");
            return null;
        }

        // Save the "config.yml" from the JAR file in a temporary directory
        File tempConfigFile;
        try {
            tempConfigFile = File.createTempFile("tempConfig", ".yml");
            Files.copy(resource, tempConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            resource.close();
        } catch (IOException e) {
            logger.error("An error occurred while copying config.yml from JAR to temporary directory", e);
            return null;
        }

        // Load the "config.yml" from the temporary directory
        return tempConfigFile;
    }

    public void extractFolderFromJar(String folderName) {
        // Get the plugin folder
        File pluginFolder = getDataFolder();

        // Create the target folder in the plugin directory
        File targetFolder = new File(pluginFolder, folderName);
        targetFolder.mkdirs();

        // Get the input stream of the JAR file
        try {
            InputStream jarInputStream = getResource(folderName);

            // Check if the folder exists in the JAR
            if (jarInputStream == null) {
                getLogger().warning("Folder not found in JAR: " + folderName);
                return;
            }

            // Copy the contents of the folder from the JAR to the plugin folder
            Files.copy(jarInputStream, targetFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}