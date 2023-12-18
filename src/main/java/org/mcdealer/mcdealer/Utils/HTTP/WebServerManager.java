package org.mcdealer.mcdealer.Utils.HTTP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServerManager {

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");
    private final JavaPlugin plugin;

    public WebServerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void JettyStart() {
        FileConfiguration config = plugin.getConfig();
        int port = config.getInt("webServerPort", 8080);
        try {
            JettyServer.start(port);
        } catch (Exception e) {
            logger.error("Failed to start Web Server!");
        }
    }

    public void JettyStop() {
        try {
            JettyServer.stop();
        } catch (Exception e) {
            logger.error("Failed to stop Web Server!");
        }
    }

    public void JettyRestart() {
        // Stop Jetty Server after 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                JettyStop();
                // Start Jetty Server after another 30 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        JettyStart();
                    }
                }.runTaskLater(plugin, 30 * 20); // 20 ticks per second, so 30 seconds * 20 ticks
            }
        }.runTaskLater(plugin, 30 * 20); // 20 ticks per second, so 30 seconds * 20 ticks
    }
}
