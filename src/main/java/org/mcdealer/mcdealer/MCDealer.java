package org.mcdealer.mcdealer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MCDealer extends JavaPlugin {
    private boolean pluginEnabled = false;

    int delayTicks = 300;
    int UpdateInterval = 300;

    @Override
    public void onEnable() {

        pluginEnabled = true;

        // Extracts necessary resources from the JAR file
        this.getLogger().info(" Check resources... ");
        new ResourceUpdater(this).updateConfig();
        new ResourceUpdater(this).updatePyScript();
        new ResourceUpdater(this).updateWebFolder();

        // Run Webserver
        new ConfigUpdater(this).webConfigUpdater();
        this.getLogger().info(" Starting Webserver ");
        WebServer webServer = new WebServer(this);
        webServer.RunWebServer();

        this.getLogger().info(" [MCDealer] by CptGummiball and Vollmondheuler enabled! ");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pluginEnabled) {
                    try {
                        // Your existing code here
                        ShopDataConverter.main(new String[]{});
                    } catch (Exception e) {
                        getLogger().info(" Converter failed! ");
                    }
                }
            }
        }.runTaskTimer(this, delayTicks, UpdateInterval);
    }

    @Override
    public void onDisable() {

        new WebServer(this).stopWebServer();
        pluginEnabled = false;
        this.getLogger().info(" [MCDealer] by CptGummiball and Vollmondheuler disabled! ");

    }
}