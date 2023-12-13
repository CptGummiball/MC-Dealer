package org.mcdealer.mcdealer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConfigUpdater extends MCDealer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    public ConfigUpdater(MCDealer plugin) {
    }

    public void webConfigUpdater() {
        // Load the configurations
        FileConfiguration jsonConfig = loadJsonConfig();
        FileConfiguration yamlConfig = getConfig();

        // Synchronize the values of the configurations
        syncValues(yamlConfig, jsonConfig);

        // Save the updated JSON configuration
        saveJsonConfig(jsonConfig);
    }

    private FileConfiguration loadJsonConfig() {
        File configFile = new File(getDataFolder(), "/web/assets/config.json");
        return YamlConfiguration.loadConfiguration(configFile);
    }

    private void syncValues(FileConfiguration yamlConfig, FileConfiguration jsonConfig) {
        // Synchronize the values
        jsonConfig.set("currencySymbol", yamlConfig.getString("currencySymbol"));
        jsonConfig.set("currencySymbolPosition", yamlConfig.getString("currencySymbolPosition"));
        jsonConfig.set("defaultLanguage", yamlConfig.getString("defaultLanguage"));
    }

    private void saveJsonConfig(FileConfiguration jsonConfig) {
        File configFile = new File(getDataFolder(), "/web/assets/config.json");
        try {
            jsonConfig.save(configFile);
        } catch (IOException e) {
            logger.warn(" Json could not be saved ");
        }
    }
}