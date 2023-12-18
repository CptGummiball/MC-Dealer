package org.mcdealer.mcdealer;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.mcdealer.mcdealer.Utils.HTTP.WebServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.mcdealer.mcdealer.Utils.DataMangager.ResourceUpdater;
import org.mcdealer.mcdealer.Utils.DataMangager.ConfigUpdater;
import org.mcdealer.mcdealer.Utils.JythonScriptRunner;
import org.mcdealer.mcdealer.Utils.ShopHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCDealer extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    private WebServerManager webServerManager;
    private ShopHandler shopHandler;
    private ConfigUpdater configUpdater;
    private boolean pluginEnabled = false;
    int delayTicks = 2400;
    protected int UpdateInterval;


    @Override
    public void onEnable() {

        pluginEnabled = true;
        shopHandler = new ShopHandler(this);
        getServer().getPluginManager().registerEvents(shopHandler, this);

        // Extracts necessary resources from the JAR file
        new ResourceUpdater(this).updateWebFolder();
        new ResourceUpdater(this).updateConfig();
        // Run Webserver
        logger.info("Starting Webserver");
        configUpdater = new ConfigUpdater(this);
        configUpdater.webConfigUpdater();
        webServerManager = new WebServerManager(this);
        webServerManager.JettyStart();
        // Register Commands
        getCommand("mcdealer").setExecutor(new MCDealerCommand());
        // Initialize the scheduler
        logger.info("[MCDealer] by CptGummiball and Vollmondheuler enabled!");
        // Initialize the scheduler
        loadConfig();
        initScheduler();
    }

    @Override
    public void onDisable() {

        pluginEnabled = false;
        webServerManager.JettyStop();
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
                        configUpdater.webConfigUpdater();
                        JythonScriptRunner.runPythonScript();
                        logger.info("Shopdata updated!");
                    } catch (Exception e) {
                        logger.error("Scheduler failed!");
                    }
                }
            }
        }.runTaskTimer(this, delayTicks, UpdateInterval);
    }

    private class MCDealerCommand implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "This command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;

            // Check the main permission
            if (!player.hasPermission("mcdealer.use")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "You do not have permission for this command.");
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "Usage: /mcdealer <getshopuuid|removeshopuuid|restart>");
                return true;
            }

            // Check the first argument (suffix) and call the corresponding method
            switch (args[0].toLowerCase()) {
                case "hideshop":
                    if (player.hasPermission("mcdealer.hideshop")) {
                        shopHandler.handleGetShopUUID(player);
                    } else {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "You do not have permission for this command.");
                    }
                    break;
                case "showshop":
                    if (player.hasPermission("mcdealer.showshop")) {
                        shopHandler.handleRemoveShopUUID(player);
                    } else {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "You do not have permission for this command.");
                    }
                    break;
                case "listhidden":
                    if (player.hasPermission("mcdealer.list")) {
                        shopHandler.handleCheckHiddenShops(player);
                    } else {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "You do not have permission for this command.");
                    }
                    break;
                case "restart":
                    if (player.hasPermission("mcdealer.restart")) {
                        webServerManager.JettyRestart();
                    } else {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "You do not have permission for this command");
                    }
                    break;
                default:
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.RED + "Unknown argument: " + args[0]);
                    break;
            }

            return true;
        }
    }
}
