package org.mcdealer.mcdealer.Utils.DataManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.plugin.Plugin;
import org.mcdealer.mcdealer.MCDealer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigUpdater {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");

    private final MCDealer plugin;

    public ConfigUpdater(MCDealer plugin) {
        this.plugin = plugin;
    }

    public void webConfigUpdater() {
        // Load the configurations
        JSONObject jsonConfig = loadJsonConfig();
        FileConfiguration yamlConfig = getConfig();
        FileConfiguration villagerMarketConfig = getVillagerMarketConfig();

        if (villagerMarketConfig == null) {
            return;  // Exit if VillagerMarket plugin not found or config not loaded
        }

        // Synchronize the values of the configurations
        syncValues(yamlConfig, jsonConfig, villagerMarketConfig);

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

    private void syncValues(FileConfiguration yamlConfig, JSONObject jsonObj, FileConfiguration villagerMarketConfig) {
        // Synchronize the values from VillagerMarket plugin config
        String currencySymbol = villagerMarketConfig.getString("currency");
        String currencySymbolPosition = villagerMarketConfig.getString("currency_before");

        // Map the values from VillagerMarket config to the expected format
        if (currencySymbolPosition != null) {
            if (currencySymbolPosition.equalsIgnoreCase("true")) {
                currencySymbolPosition = "before";
            } else if (currencySymbolPosition.equalsIgnoreCase("false")) {
                currencySymbolPosition = "after";
            }
        }

        // Set the values in the JSON config
        jsonObj.put("currencySymbol", currencySymbol);
        jsonObj.put("currencySymbolPosition", currencySymbolPosition);
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

    private FileConfiguration getVillagerMarketConfig() {
        Plugin villagerMarketPlugin = Bukkit.getPluginManager().getPlugin("VillagerMarket");

        if (villagerMarketPlugin != null) {
            return villagerMarketPlugin.getConfig();
        } else {
            logger.warn("VillagerMarket plugin not found");
            return null;
        }
    }

    public static void pluginconfigupdater(FileConfiguration oldConfig, FileConfiguration newConfig) {
        Map<String, Object> updates = new HashMap<>();
        for (String key : newConfig.getKeys(true)) {

            if (!oldConfig.contains(key)) {

                updates.put(key, newConfig.get(key));
            }
        }
        for (String key : oldConfig.getKeys(true)) {

            if (!newConfig.contains(key)) {

                updates.put(key, null);
            }
        }
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            oldConfig.set(entry.getKey(), entry.getValue());
        }
    }
}
