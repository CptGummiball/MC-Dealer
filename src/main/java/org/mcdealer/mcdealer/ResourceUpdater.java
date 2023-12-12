package org.mcdealer.mcdealer;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;
import java.util.logging.Level;

import static org.mcdealer.mcdealer.ResourceUtils.copyResources;

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
            this.getLogger().info("updating config.yml... ");
            plugin.saveResource("config.yml", true);
        }
    }

    void updateWebFolder() {
        // Check and update the "web" folder

        int currentWebFilesVersion = getConfig().getInt("webfilesversion", 0);
        int newWebFilesVersion = YamlConfiguration.loadConfiguration(Objects.requireNonNull(getConfigFromJar())).getInt("webfilesversion", 0);

        if (newWebFilesVersion > currentWebFilesVersion) {
            // Version difference, update web files
            this.getLogger().info("updating webfiles... ");
            copyResources("web/resource_list.txt", "/path/to/destination");
            copyResources("web/assets", "/path/to/destination");
            copyResources("web/assets/favicon/resource_list.txt", "/path/to/destination");
            copyResources("web/assets/items/resource_list.txt", "/path/to/destination");
            copyResources("web/assets/items/joshs-more-foods/resource_list.txt", "/path/to/destination");
            copyResources("web/assets/translations/resource_list.txt", "/path/to/destination");
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
                this.getLogger().info("updating script... ");
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
}