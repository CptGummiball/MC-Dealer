package org.mcdealer.mcdealer.Utils;

import org.bukkit.Bukkit;
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
            player.sendMessage("Shop UUID erfolgreich in die Datei geschrieben.");
        } else {
            player.sendMessage("Du schaust nicht auf einen Shop.");
        }
    }

    public void handleRemoveShopUUID(Player player) {
        Villager villager = getTargetVillager(player);
        if (villager != null) {
            UUID shopUUID = villager.getUniqueId();
            removeUUIDFromFile(shopUUID);
            player.sendMessage("Shop UUID erfolgreich aus der Datei entfernt.");
        } else {
            player.sendMessage("Du schaust nicht auf einen Shop.");
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
        File file = new File(plugin.getDataFolder(), "shop_uuids.json");

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
                    player.sendMessage("Dieser Shop ist bereits versteckt.");
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
        File file = new File(plugin.getDataFolder(), "shop_uuids.json");

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