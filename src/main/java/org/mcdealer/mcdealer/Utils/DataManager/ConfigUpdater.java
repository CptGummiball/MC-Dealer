package org.mcdealer.mcdealer.Utils.DataManager;

import org.bukkit.configuration.file.FileConfiguration;

import org.mcdealer.mcdealer.MCDealer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigUpdater {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer (ConfigUpdater)");

    private final MCDealer plugin;

    public ConfigUpdater(MCDealer plugin) {
        this.plugin = plugin;
    }

    public void webConfigUpdater() {
        // Load the configurations
        JSONObject jsonConfig = loadJsonConfig();
        FileConfiguration yamlConfig = getConfig();

        // Synchronize the values of the configurations
        syncValues(yamlConfig, jsonConfig);

        // Save the updated JSON configuration
        saveJsonConfig(jsonConfig);
    }

    private JSONObject loadJsonConfig() {
        File configFile = new File(plugin.getDataFolder(), "/web/assets/config.json");

        try (FileReader reader = new FileReader(configFile)) {
            return new JSONObject(new JSONTokener(reader));
        } catch (IOException e) {
            logger.warn("Json could not be loaded", e);
            return new JSONObject();
        }
    }

    private void syncValues(FileConfiguration yamlConfig, JSONObject jsonObj) {
        // Synchronize the values
        jsonObj.put("currencySymbol", yamlConfig.getString("currencySymbol"));
        jsonObj.put("currencySymbolPosition", yamlConfig.getString("currencySymbolPosition"));
        jsonObj.put("defaultLanguage", yamlConfig.getString("defaultLanguage"));
    }

    private void saveJsonConfig(JSONObject jsonObj) {
        File configFile = new File(plugin.getDataFolder(), "/web/assets/config.json");

        try (FileWriter writer = new FileWriter(configFile)) {
            jsonObj.write(writer);
        } catch (IOException e) {
            logger.warn("Json could not be saved", e);
        }
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}
