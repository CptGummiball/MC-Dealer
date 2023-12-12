package org.mcdealer.mcdealer;
import org.mcdealer.mcdealer.commands.RestartWebServerCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MCDealer extends JavaPlugin {

    @Override
    public void onEnable() {

        // Extracts necessary resources from the JAR file
        this.getLogger().info(" MCDealer: Check resources... ");
        new ResourceUpdater(this).updateConfig();
        new ResourceUpdater(this).updateWebFolder();
        new ResourceUpdater(this).updatePyScript();
        new ConfigUpdater(this).webConfigUpdater();

        // Run Webserver
        this.getLogger().info(" MCDealer: Starting Webserver ");
        WebServer webServer = new WebServer(this);
        webServer.RunWebServer();
        Objects.requireNonNull(getCommand("restartwebserver")).setExecutor(new RestartWebServerCommand(webServer));

        this.getLogger().info(" MCDealer by CptGummiball and Vollmondheuler enabled! ");

        // Schedule the recurring task based on the interval
        new TaskScheduler(this).loadConfig();
        new TaskScheduler(this).scheduleRepeatingTask();
    }

    @Override
    public void onDisable() {

        new WebServer(this).stopWebServer();
        this.getLogger().info(" MCDealer by CptGummiball and Vollmondheuler disabled! ");

    }
}
