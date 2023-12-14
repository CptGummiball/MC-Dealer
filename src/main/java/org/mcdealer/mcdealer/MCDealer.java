package org.mcdealer.mcdealer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCDealer extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    private boolean pluginEnabled = false;
    int delayTicks = 300;
    private int UpdateInterval;

    @Override
    public void onEnable() {

        pluginEnabled = true;

        // Extracts necessary resources from the JAR file
        new ResourceUpdater(this).updateWebFolder();
        new ResourceUpdater(this).updateConfig();

        // Run Webserver
        new ConfigUpdater(this).webConfigUpdater();
        logger.info(ColorUtils.format("&#FFE800Starting Webserver"));
        WebServer webServer = new WebServer(this);
        webServer.RunWebServer();
        logger.info(ColorUtils.format("&#CC00FF[MCDealer] by CptGummiball and Vollmondheuler enabled!"));

        // Initialize the scheduler
        loadConfig();
        initScheduler();
    }

    private void loadConfig() {
        // Load config for UpdateInterval
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Read the value of UpdateInterval from the configuration
        UpdateInterval = getConfig().getInt("UpdateInterval", 300);
    }

    private void initScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pluginEnabled) {
                    try {
                        loadConfig();
                        setNewWebSettings();
                        new executePython(this).executePythonScript();
                    } catch (Exception e) {
                        logger.error("Converter failed!");
                    }
                }
            }
        }.runTaskTimer(this, delayTicks, UpdateInterval);
    }

    public void setNewWebSettings() {
        new ConfigUpdater(this).webConfigUpdater();
    }

    @Override
    public void onDisable() {

        new WebServer(this).stopWebServer();
        pluginEnabled = false;
        logger.info(ColorUtils.format("&#CC00FF[MCDealer] by CptGummiball and Vollmondheuler disabled!"));

    }
}