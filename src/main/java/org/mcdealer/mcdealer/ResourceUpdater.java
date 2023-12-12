package org.mcdealer.mcdealer;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.*;
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
            this.getLogger().info(" [MCDealer] updating config.yml... ");
            plugin.saveResource("config.yml", true);
        }
    }

    void updateWebFolder() {
        // Check and update the "web" folder

        int currentWebFilesVersion = getConfig().getInt("webfilesversion", 0);
        int newWebFilesVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("webfilesversion", 0);

        if (newWebFilesVersion > currentWebFilesVersion) {
            // Version difference, update web files
            this.getLogger().info(" [MCDealer] updating webfiles... ");
            plugin.saveResource("web/index.html", true);
            plugin.saveResource("web/assets/background.jpg", true);
            plugin.saveResource("web/assets/logo.png", true);
            plugin.saveResource("web/assets/Minecraftia-Regular.woff", true);
            plugin.saveResource("web/assets/Minecraftia-Regular.woff2", true);
            plugin.saveResource("web/assets/script.js", true);
            plugin.saveResource("web/assets/style.css", true);
            plugin.saveResource("web/assets/favicon/*.png", true);
            plugin.saveResource("web/assets/favicon/browserconfig.xml", true);
            plugin.saveResource("web/assets/favicon/favicon.ico", true);
            plugin.saveResource("web/assets/favicon/site.webmanifest", true);
            plugin.saveResource("web/assets/items/*.png", true);
            plugin.saveResource("web/assets/items/joshs-more-foods/*.png", true);
            plugin.saveResource("web/assets/translations/*.svg", true);
            plugin.saveResource("web/assets/translations/*.json", true);
            // Update the config with the new version
            getConfig().set("webfilesversion", newWebFilesVersion);
            saveConfig();
        }
    }

    void updatePyScript() {
        //Loading the current configuration file
        int currentPyScriptVersion = getConfig().getInt("pyscriptversion", 0);
        int newPyScriptVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("pyscriptversion", 0);

        if (!plugin.getDataFolder().exists() || !new File(plugin.getDataFolder(), "data-yml2json.py").exists()) {
            plugin.saveResource("data-yml2json.py", false);

            if (newPyScriptVersion > currentPyScriptVersion) {
                // Version difference, update configuration file
                this.getLogger().info(" [MCDealer] updating script... ");
                plugin.saveResource("data-yml2json.py", true);
                // Update the config with the new version
                getConfig().set("pyscriptversion", newPyScriptVersion);
                saveConfig();
            }
        }
    }

    private File getConfigFromJar() {
        // Loading the "config.yml" from the JAR file
        InputStream resource = getResource("config.yml");

        // Check if the resource is null
        if (resource == null) {
            plugin.getLogger().severe("[MCDealer] Unable to find config.yml in the JAR file.");
            return null;
        }

        // Save the "config.yml" from the JAR file in a temporary directory
        File tempConfigFile;
        try {
            tempConfigFile = File.createTempFile("tempConfig", ".yml");
            Files.copy(resource, tempConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            resource.close();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "[MCDealer] An error occurred while copying config.yml from JAR to temporary directory", e);
            return null;
        }

        // Load the "config.yml" from the temporary directory
        return tempConfigFile;
    }
}