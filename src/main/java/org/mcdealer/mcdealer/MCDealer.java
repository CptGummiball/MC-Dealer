package org.mcdealer.mcdealer;

import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.mcdealer.mcdealer.Utils.HTTP.WebServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.mcdealer.mcdealer.Utils.DataManager.ResourceUpdater;
import org.mcdealer.mcdealer.Utils.DataManager.ConfigUpdater;
import org.mcdealer.mcdealer.Utils.JythonScriptRunner;
import org.mcdealer.mcdealer.Utils.ShopHandler;
import org.mcdealer.mcdealer.Utils.Translator;

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

        // Load config
        loadConfig();

        // Load languages
        Translator translator = Translator.getInstance();
        translator.loadLanguages(this);

        // Extracts necessary resources from the JAR file
        new ResourceUpdater(this).updateWebFolder();
        new ResourceUpdater(this).updateConfig();

        // Initialize other components
        shopHandler = new ShopHandler(this);
        getServer().getPluginManager().registerEvents(shopHandler, this);
        webServerManager = new WebServerManager(this);

        // Run Webserver
        logger.info("Starting Webserver");
        configUpdater = new ConfigUpdater(this);
        configUpdater.webConfigUpdater();
        webServerManager.jettyStart();

        // Register Commands
        registerCommands();

        logger.info("[MCDealer] by CptGummiball and Vollmondheuler enabled!");
        // Initialize the scheduler
        loadConfig();
        initScheduler();
    }


    @Override
    public void onDisable() {

        pluginEnabled = false;
        if (webServerManager != null) {
            webServerManager.jettyStop();
        }
        logger.info("[MCDealer] by CptGummiball and Vollmondheuler disabled!");
    }

    private void loadConfig() {
        // Load config for UpdateInterval
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        // Read the value of UpdateInterval from the configuration
        UpdateInterval = getConfig().getInt("UpdateInterval", 6000);
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
        }.runTaskTimerAsynchronously(this, delayTicks, UpdateInterval);
    }

    private class MCDealerCommand implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
            Translator translator = Translator.getInstance();
            if (!(sender instanceof Player)) {
                sender.sendMessage(translator.translate("Commands.onlyplayer"));
                return true;
            }

            Player player = (Player) sender;

            // Check the main permission
            if (!player.hasPermission("mcdealer.use")) {
                player.sendMessage(translator.translate("Commands.nopermission"));
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(translator.translate("Commands.args"));
                return true;
            }

            // Check the first argument (suffix) and call the corresponding method
            switch (args[0].toLowerCase()) {
                case "hideshop":
                    if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.hideshop")) {
                        Villager villager = shopHandler.getTargetVillager(player);

                        if (villager != null) {
                            if (ShopHandler.isShopOwner(player, villager)) {
                                shopHandler.handleGetShopUUID(player);
                            } else {
                                player.sendMessage(translator.translate("Commands.notshopowner"));
                            }
                        } else {
                            player.sendMessage(translator.translate("Commands.novillagerfound"));
                        }
                    } else {
                        player.sendMessage(translator.translate("Commands.nopermission"));
                    }
                    break;
                case "showshop":
                    if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.showshop")) {
                        Villager villager = shopHandler.getTargetVillager(player);

                        if (villager != null) {
                            if (ShopHandler.isShopOwner(player, villager)) {
                                shopHandler.handleRemoveShopUUID(player);
                            } else {
                                player.sendMessage(translator.translate("Commands.notshopowner"));
                            }
                        } else {
                            player.sendMessage(translator.translate("Commands.novillagerfound"));
                        }
                    } else {
                        player.sendMessage(translator.translate("Commands.nopermission"));
                    }
                    break;
                case "listhidden":
                    if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.list")) {
                        shopHandler.handleCheckHiddenShops(player);
                    } else {
                        player.sendMessage(translator.translate("Commands.nopermission"));
                    }
                    break;
                case "restart":
                    if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.restart")) {
                        webServerManager.jettyRestart();
                        player.sendMessage(translator.translate("Commands.restart"));
                    } else {
                        player.sendMessage(translator.translate("Commands.nopermission"));
                    }
                    break;
                default:
                    player.sendMessage(translator.translate("Commands.unknownarg") + args[0]);
                    break;
            }

            return true;
        }
    }

    private void registerCommands() {
        getCommand("mcdealer").setExecutor(new MCDealerCommand());
    }
}