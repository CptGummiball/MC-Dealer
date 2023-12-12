package org.mcdealer.mcdealer;

import org.bukkit.plugin.java.JavaPlugin;

public class MCDealer extends JavaPlugin {

    @Override
    public void onEnable() {

        // Extracts necessary resources from the JAR file
        this.getLogger().info(" [MCDealer] Check resources... ");
        new ResourceUpdater(this).updateConfig();
        new ResourceUpdater(this).updatePyScript();
        new ResourceUpdater(this).updateWebFolder();

        // Run Webserver
        new ConfigUpdater(this).webConfigUpdater();
        this.getLogger().info(" [MCDealer] Starting Webserver ");
        WebServer webServer = new WebServer(this);
        webServer.RunWebServer();

        this.getLogger().info(" [MCDealer] by CptGummiball and Vollmondheuler enabled! ");

        // Schedule the recurring task based on the interval
        new TaskScheduler(this).loadConfig();
        int delay = 300;
        getServer().getScheduler().runTaskLater(this, new TaskScheduler(this).scheduleRepeatingTask(), delay);
        new TaskScheduler(this).scheduleRepeatingTask();
    }

    @Override
    public void onDisable() {

        new WebServer(this).stopWebServer();
        this.getLogger().info(" [MCDealer] by CptGummiball and Vollmondheuler disabled! ");

    }
}
