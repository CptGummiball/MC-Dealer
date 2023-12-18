package org.mcdealer.mcdealer.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

public class ShopHandler implements Listener {

    private final JavaPlugin plugin;

    private static final Logger logger = LoggerFactory.getLogger("MCDealer");

    public ShopHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleGetShopUUID(Player player) {
        Villager villager = getTargetVillager(player);
        if (villager != null) {
            UUID shopUUID = villager.getUniqueId();
            writeUUIDToFile(shopUUID);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "Shop is no longer displayed in the shop overview.");
        } else {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "You're not looking at a shop.");
        }
    }

    public void handleRemoveShopUUID(Player player) {
        Villager villager = getTargetVillager(player);
        if (villager != null) {
            UUID shopUUID = villager.getUniqueId();
            removeUUIDFromFile(shopUUID);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "Shop is now displayed again in the shop overview.");
        } else {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "You're not looking at a shop.");
        }
    }

    public void handleCheckHiddenShops(Player player) {
        File hiddenShopsFile = new File(plugin.getDataFolder(), "web/hidden_shops.json");
        File outputShopsFile = new File(plugin.getDataFolder(), "web/output.json");

        try (FileReader hiddenReader = new FileReader(hiddenShopsFile);
             FileReader outputReader = new FileReader(outputShopsFile)) {

            JSONArray hiddenArray = new JSONArray(new JSONTokener(hiddenReader));
            JSONArray outputArray = new JSONArray(new JSONTokener(outputReader));

            for (int i = 0; i < hiddenArray.length(); i++) {
                JSONObject hiddenObject = hiddenArray.getJSONObject(i);
                String hiddenUUID = hiddenObject.getString("shop_uuid");

                for (int j = 0; j < outputArray.length(); j++) {
                    JSONObject outputObject = outputArray.getJSONObject(j);
                    String outputUUID = outputObject.getString("shop_uuid");

                    if (hiddenUUID.equals(outputUUID)) {
                        String shopName = outputObject.getString("shop_name");
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "Hidden Shops: " + shopName);
                        break;
                    }
                }
            }

        } catch (IOException | JSONException e) {
            logger.error("Error checking hidden shops", e);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Villager villager = getTargetVillager(player);
        if (villager != null) {
            player.sendMessage("Shop UUID: " + villager.getUniqueId());
        }
    }

    private Villager getTargetVillager(Player player) {
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Villager) {
                return (Villager) entity;
            }
        }
        return null;
    }

    private void writeUUIDToFile(UUID shopUUID) {
        File file = new File(plugin.getDataFolder(), "web/hidden_shops.json");

        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader);
             FileWriter writer = new FileWriter(file)) {

            JSONArray jsonArray;

            try {
                jsonArray = new JSONArray(bufferedReader.readLine());
            } catch (JSONException | IOException e) {
                jsonArray = new JSONArray();
            }

            if (!isUUIDInList(shopUUID, jsonArray)) {
                jsonArray.put(new JSONObject().put("shop_uuid", shopUUID.toString()));
                writer.write(jsonArray.toString(2));
            } else {
                Player player = Bukkit.getPlayer(shopUUID);
                if (player != null) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "[MCDealer]" + ChatColor.YELLOW + "This shop is already hidden in the shop overview.");
                }
            }

        } catch (IOException e) {
            logger.error("Error writing to file", e);
        }
    }

    private boolean isUUIDInList(UUID shopUUID, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String existingUUID = jsonObject.getString("shop_uuid");
            if (existingUUID.equals(shopUUID.toString())) {
                return true;
            }
        }
        return false;
    }

    private void removeUUIDFromFile(UUID shopUUID) {
        File file = new File(plugin.getDataFolder(), "web/hidden_shops.json");

        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader);
             FileWriter writer = new FileWriter(file)) {

            JSONArray jsonArray;

            try {
                jsonArray = new JSONArray(bufferedReader.readLine());
            } catch (JSONException | IOException e) {
                return;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String existingUUID = jsonObject.getString("shop_uuid");
                if (existingUUID.equals(shopUUID.toString())) {
                    jsonArray.remove(i);
                    break;
                }
            }

            writer.write(jsonArray.toString(2));

        } catch (IOException e) {
            logger.error("Error writing to file", e);
        }
    }
}