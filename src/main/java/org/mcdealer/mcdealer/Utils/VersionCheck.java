package org.mcdealer.mcdealer.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class VersionCheck {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");

    public void getVersion() {
        serverversion();
        vmversion();
        pluginversion();
        writeVersionInfoToFile("plugins/MCDealer/logs/version.txt");

    }

    public void writeVersionInfoToFile(String filePath) {
        File file = new File(filePath);

        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                logger.error("Error creating directory for file: " + file.getParentFile().getAbsolutePath());
                return;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writeToFile(writer, "serverversion");
            writeToFile(writer, "vmversion");
            writeToFile(writer, "pluginversion");
        } catch (IOException e) {
            logger.error("Error writing version information to file", e);
        }
    }

    public void serverversion() {
        String serverType = getServerType();
        String bukkitVersion = Bukkit.getBukkitVersion();
        logger.info("Serverversion: " + serverType + bukkitVersion);
        if (maynotcompatible(serverType)) {
            logger.warn("MCDealer may not compatible with " + serverType + " Please use at your own risk!");
            logger.warn("Please report issues to https://github.com/CptGummiball/MC-Dealer/issues");
        }
    }

    public void vmversion() {
        Plugin vmPlugin = Bukkit.getPluginManager().getPlugin("Villagermarket");
        if (vmPlugin != null) {
            String vmVersion = vmPlugin.getDescription().getVersion();
            logger.info("Villagermarket Version: " + vmVersion);
        } else {
            logger.warn("Villagermarket not found!");
        }
    }

    public void pluginversion() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MCDealer");
        if (plugin != null) {
            String pluginVersion = plugin.getDescription().getVersion();
            logger.info("MCDealer Version: " + pluginVersion);
            if (isBetaVersion(pluginVersion)) {
                logger.warn("This is a beta version of MCDealer. Please use at your own risk.");
            }
        } else {
            logger.warn("MCDealer Plugin Version not found!");
        }
    }

    private boolean isBetaVersion(String version) {
        return version.contains("dev") ||
                version.toLowerCase().contains("beta") ||
                version.toLowerCase().contains("alpha");
    }

    private String getServerType() {
        String serverVersion = Bukkit.getServer().getVersion().toLowerCase();

        if (serverVersion.contains("spigot")) {
            return "Spigot ";
        } else if (serverVersion.contains("craftbukkit")) {
            return "CraftBukkit ";
        } else if (serverVersion.contains("paper")) {
            return "PaperMC ";
        } else if (serverVersion.contains("tuinity")) {
            return "Tuinity ";
        } else if (serverVersion.contains("taco")) {
            return "TacoSpigot ";
        } else if (serverVersion.contains("yatopia")) {
            return "Yatopia ";
        } else if (serverVersion.contains("torch")) {
            return "Torch ";
        } else if (serverVersion.contains("sponge")) {
            return "Sponge ";
        } else {
            return "Unknown Server Type ";
        }
    }

    private boolean maynotcompatible(String serverType) {
        return serverType.equalsIgnoreCase("Tuinity") ||
                serverType.equalsIgnoreCase("Tacospigot") ||
                serverType.equalsIgnoreCase("Yatopia") ||
                serverType.equalsIgnoreCase("Torch") ||
                serverType.equalsIgnoreCase("Sponge");
        }

    private void writeToFile(PrintWriter writer, String section) {
        switch (section) {
            case "serverversion":
                writer.println("Serverversion: " + getServerType() + Bukkit.getBukkitVersion());
                break;
            case "vmversion":
                Plugin vmPlugin = Bukkit.getPluginManager().getPlugin("Villagermarket");
                if (vmPlugin != null) {
                    writer.println("Villagermarket Version: " + vmPlugin.getDescription().getVersion());
                } else {
                    writer.println("Villagermarket not found!");
                }
                break;
            case "pluginversion":
                Plugin plugin = Bukkit.getPluginManager().getPlugin("MCDealer");
                if (plugin != null) {
                    String pluginVersion = plugin.getDescription().getVersion();
                    writer.println("MCDealer Version: " + pluginVersion);
                } else {
                    writer.println("MCDealer Plugin Version not found!");
                }
                break;
            default:
                break;
        }
    }
}
