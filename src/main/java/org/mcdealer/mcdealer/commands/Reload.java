package org.mcdealer.mcdealer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mcdealer.mcdealer.MCDealer;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {

    private final @NotNull MCDealer plugin;

    public Reload(MCDealer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mcdealer.reload")) {
                sender.sendMessage("You do not have the required permissions to run this command.");
                return true;
            }

            plugin.ConfigUpdater.webConfigUpdater();
            plugin.WebServer.restartWebServer();
            sender.sendMessage("Webserver restarted");
            return true;
        }
        return false;
    }
}