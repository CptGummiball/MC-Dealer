package org.mcdealer.mcdealer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mcdealer.mcdealer.WebServer;

public class RestartWebServerCommand implements CommandExecutor {

    private final WebServer webServer;

    public RestartWebServerCommand(WebServer webServer) {
        this.webServer = webServer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("restartwebserver")) {
            if (sender.hasPermission("mcdealer.reload")) {
                webServer.restartWebServer();
                sender.sendMessage("Web server restarted successfully.");
                return true;
            } else {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
        }
        return false;
    }
}