package org.mcdealer.mcdealer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MCDealer extends JavaPlugin {
    private boolean pluginEnabled = false;

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

        // Schedule the recurring task with a delay after the server has loaded
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (pluginEnabled) {
                new TaskScheduler(this).loadConfig();
                new TaskScheduler(this).scheduleRepeatingTask();
            }
        }, 20L); // 20 ticks delay (1 second = 20 ticks)
    }

    @Override
    public void onDisable() {

        new WebServer(this).stopWebServer();
        pluginEnabled = false;
        this.getLogger().info(" [MCDealer] by CptGummiball and Vollmondheuler disabled! ");

    }
}
