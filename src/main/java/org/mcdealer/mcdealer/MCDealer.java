package org.mcdealer.mcdealer;

import org.mcdealer.mcdealer.Utils.DataMangager.ResourceUpdater;
import org.mcdealer.mcdealer.Utils.DataMangager.ConfigUpdater;
import org.mcdealer.mcdealer.Utils.HTTP.JettyServer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.mcdealer.mcdealer.Utils.JythonScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCDealer extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");

    private boolean pluginEnabled = false;
    int delayTicks = 300;
    protected int UpdateInterval;

    @Override
    public void onEnable() {

        pluginEnabled = true;

        // Extracts necessary resources from the JAR file
        new ResourceUpdater(this).updateWebFolder();
        new ResourceUpdater(this).updateConfig();
        // Run Webserver
        logger.info("Starting Webserver");
        setNewWebSettings();
        JettyStart();
        logger.info("[MCDealer] by CptGummiball and Vollmondheuler enabled!");
        // Initialize the scheduler
        loadConfig();
        initScheduler();
    }

    @Override
    public void onDisable() {

        pluginEnabled = false;
        JettyStop();
        logger.info("[MCDealer] by CptGummiball and Vollmondheuler disabled!");
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
                        JythonScriptRunner.runPythonScript();
                        logger.info("Shopdata updated!");
                    } catch (Exception e) {
                        logger.error("Scheduler failed!");
                    }
                }
            }
        }.runTaskTimer(this, delayTicks, UpdateInterval);
    }

    public void setNewWebSettings() {
        new ConfigUpdater(this).webConfigUpdater();
    }

    public void JettyStart() {
        int port = getConfig().getInt("webServerPort", 8080);
        try {
            JettyServer.start(port);
        } catch (Exception e) {
            logger.error("Failed to start Jetty Server!");
        }
    }

    public void JettyStop() {
        try {
            JettyServer.stop();
        } catch (Exception e) {
            logger.error("Failed to stop Jetty Server!");
        }
    }
}