package org.mcdealer.mcdealer.Utils.DataManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.mcdealer.mcdealer.MCDealer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ResourceUpdater {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer (ResourceUpdater)");
    private final MCDealer plugin;

    public ResourceUpdater(MCDealer plugin) {
        this.plugin = plugin;
    }

    public void updateConfig() {
        // Loading the current configuration file
        int currentConfigVersion = plugin.getConfig().getInt("configversion", 0);
        int newConfigVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("configversion", 0);
        logger.info("Current config version: {}", currentConfigVersion);
        logger.info("New config version: {}", newConfigVersion);
        // Version difference, update configuration file
        if (newConfigVersion > currentConfigVersion) {
            logger.info("Updating config.yml... ");
            plugin.saveResource("config.yml", true);
        }
    }

    public void updateWebFolder() {
        WebFileUtils webFileUtils = new WebFileUtils(plugin);
        int currentWebFilesVersion = plugin.getConfig().getInt("webfilesversion", 0);
        int newWebFilesVersion = YamlConfiguration.loadConfiguration(getConfigFromJar()).getInt("webfilesversion", 0);
        logger.info("Current web files version: {}", currentWebFilesVersion);
        logger.info("New web files version: {}", newWebFilesVersion);
        File webFolder = new File(plugin.getDataFolder(), "web");
        if (!webFolder.exists() || !webFolder.isDirectory()) {
            logger.info("Transferring all web files...");
            webFileUtils.copy();
        } else if (newWebFilesVersion > currentWebFilesVersion) {
            logger.info("Updating web files... ");
            webFileUtils.copy();
            plugin.getConfig().set("webfilesversion", newWebFilesVersion);
            plugin.saveConfig();
        }
    }

    private File getConfigFromJar() {
        // Loading the "config.yml" from the JAR file
        InputStream resource = plugin.getResource("config.yml");
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
            logger.error("An error occurred while copying config.yml from JAR to a temporary directory", e);
            return null;
        }
        // Load the "config.yml" from the temporary directory
        return tempConfigFile;
    }
}
