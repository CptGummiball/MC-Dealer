package org.mcdealer.mcdealer;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.mcdealer.mcdealer.Utils.*;
import org.mcdealer.mcdealer.Utils.DataManager.ResourceUpdater;
import org.mcdealer.mcdealer.Utils.DataManager.ConfigUpdater;
import org.mcdealer.mcdealer.Utils.DataManager.WebFileUtils;
import org.mcdealer.mcdealer.Utils.HTTP.WebServerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mcdealer.mcdealer.Utils.ExternalIPFetcher.getExternalIP;

public class MCDealer extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    private WebServerManager webServerManager;
    private ShopHandler shopHandler;
    private ConfigUpdater configUpdater;
    private WebFileUtils webFileUtils;
    private ExternalHost externalHost;
    private boolean pluginEnabled = false;
    int delayTicks = 2400;
    protected int UpdateInterval;

    @Override
    public void onEnable() {

        // Version Check
        VersionCheck versioncheck  = new VersionCheck();
        versioncheck.getVersion();

        pluginEnabled = true;
        webFileUtils = new WebFileUtils(this);

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
        // WebServer or FTP
        if (getConfig().getBoolean("ExternalHost.enabled", false)) {
            logger.info("External Host enabled!");
            logger.info("Internal web server is disabled!");
        }else{
            // Run Webserver
            logger.info("Starting Webserver");
            configUpdater = new ConfigUpdater(this);
            configUpdater.webConfigUpdater();
            webServerManager.jettyStart();
        }

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

    public void loadConfig() {
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
                    if (getConfig().getBoolean("ExternalHost.enabled", false)) {
                        try {
                            loadConfig();
                            configUpdater.webConfigUpdater();
                            JythonScriptRunner.runPythonScript();
                            externalHost = new ExternalHost(MCDealer.this);
                            externalHost.run();
                            logger.info("Shopdata updated!");
                        } catch (Exception e) {
                            logger.error("Scheduler failed!");
                        }
                        }else{
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

                }
            }.runTaskTimerAsynchronously(this, delayTicks, UpdateInterval);
    }

    private class MCDealerCommand implements CommandExecutor, TabCompleter {

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
                case "refresh":
                    if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.refresh")) {
                        webFileUtils.copy();
                        player.sendMessage(translator.translate("Commands.refresh"));
                    } else {
                        player.sendMessage(translator.translate("Commands.nopermission"));
                    }
                    break;
                case "link":
                    if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.link")) {
                        if (getConfig().getBoolean("ExternalHost.enabled", false)) {

                            String url = getConfig().getString("ExternalHost.linktoweb", "No Link definded");

                            String clickMessage = translator.translate("Commands.clickhere");

                            TextComponent clickableUrl = new TextComponent(clickMessage);
                            clickableUrl.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

                            player.spigot().sendMessage(clickableUrl);
                        }else{
                        String serverIP = getExternalIP();
                        int webServerPort = getConfig().getInt("web-server-port");

                        String url = "http://" + serverIP + ":" + webServerPort;

                        String clickMessage = translator.translate("Commands.clickhere");

                        TextComponent clickableUrl = new TextComponent(clickMessage);
                        clickableUrl.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

                        player.spigot().sendMessage(clickableUrl);
                        }
                    } else {
                        player.sendMessage(translator.translate("Commands.nopermission"));
                    }
                    break;
                case "uploadall":
                    if (getConfig().getBoolean("ExternalHost.enabled", false)) {
                        if (player.hasPermission("mcdealer.admin") || player.hasPermission("mcdealer.uploadall")) {
                            try {
                                externalHost.uploadall();
                            } catch (IOException | SftpException | JSchException e) {
                                throw new RuntimeException(e);
                            }
                            player.sendMessage(translator.translate("Commands.externalupload"));
                        } else {
                            player.sendMessage(translator.translate("Commands.nopermission"));
                        }
                    }else{
                        player.sendMessage(translator.translate("Commands.externaldisabled"));
                    }
                    break;
                default:
                    player.sendMessage(translator.translate("Commands.unknownarg") + args[0]);
                    break;
            }

            return true;
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
            List<String> completions = new ArrayList<>();

            if (args.length == 1) {
                // Provide tab completion for the first argument
                String partialCommand = args[0].toLowerCase();

                // Add your command names here
                List<String> commandNames = List.of("hideshop", "showshop", "listhidden", "restart", "refresh", "link", "uploadall");

                for (String command : commandNames) {
                    if (command.startsWith(partialCommand)) {
                        completions.add(command);
                    }
                }
            }

            // You can add more tab completion logic for additional arguments if needed

            return completions;
        }
    }


    private void registerCommands() {
        getCommand("mcdealer").setExecutor(new MCDealerCommand());
    }
}