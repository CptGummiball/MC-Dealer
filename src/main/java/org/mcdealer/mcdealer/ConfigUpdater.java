package org.mcdealer.mcdealer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUpdater extends MCDealer {

    public ConfigUpdater(MCDealer plugin) {
    }

    public void webConfigUpdater() {
        // Load the configurations
        FileConfiguration jsonConfig = loadJsonConfig();
        FileConfiguration yamlConfig = getConfig();

        // Synchronize the values of the configurations
        syncValues(jsonConfig, yamlConfig);

        // Save the updated configuration
        saveConfig();
    }

    private FileConfiguration loadJsonConfig() {
        File configFile = new File(getDataFolder(), "/web/assets/config.json");
        return YamlConfiguration.loadConfiguration(configFile);
    }

    private void syncValues(FileConfiguration jsonConfig, FileConfiguration yamlConfig) {
        // Synchronize the values
        yamlConfig.set("currencySymbol", jsonConfig.getString("currencySymbol"));
        yamlConfig.set("currencySymbolPosition", jsonConfig.getString("currencySymbolPosition"));
        yamlConfig.set("defaultLanguage", jsonConfig.getString("defaultLanguage"));
    }
}
