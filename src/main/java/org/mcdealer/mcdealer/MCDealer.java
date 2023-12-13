package org.mcdealer.mcdealer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MCDealer extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);
    private boolean pluginEnabled = false;

    int delayTicks = 300;
    int UpdateInterval = 300;

    @Override
    public void onEnable() {

        pluginEnabled = true;

        // Extracts necessary resources from the JAR file
        logger.info(" Check resources... ");
        new ResourceUpdater(this).updateConfig();
        new ResourceUpdater(this).updateWebFolder();

        // Run Webserver
        new ConfigUpdater(this).webConfigUpdater();
        logger.info(" Starting Webserver ");
        WebServer webServer = new WebServer(this);
        webServer.RunWebServer();
        logger.info(" [MCDealer] by CptGummiball and Vollmondheuler enabled! ");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (pluginEnabled) {
                    try {
                        // Your existing code here
                        new executePython(this).executePythonScript();
                    } catch (Exception e) {
                        logger.info(" Converter failed! ");
                    }
                }
            }
        }.runTaskTimer(this, delayTicks, UpdateInterval);
    }

    @Override
    public void onDisable() {

        new WebServer(this).stopWebServer();
        pluginEnabled = false;
        logger.info(" [MCDealer] by CptGummiball and Vollmondheuler disabled! ");

    }
}