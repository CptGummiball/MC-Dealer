package org.mcdealer.mcdealer.Utils;

import com.github.bestem0r.villagermarket.shops.Shop;
import com.github.bestem0r.villagermarket.shops.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class ShopHandler implements Listener {

    private final JavaPlugin plugin;

    public ShopHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleGetShopUUID(Player player) {
        Shop shop = getTargetShop(player);
        if (shop != null) {
            UUID shopUUID = shop.getUniqueId();
            writeUUIDToFile(shopUUID);
            player.sendMessage("Shop UUID erfolgreich in die Datei geschrieben.");
        } else {
            player.sendMessage("Du schaust nicht auf einen Shop.");
        }
    }

    public void handleRemoveShopUUID(Player player) {
        Shop shop = getTargetShop(player);
        if (shop != null) {
            UUID shopUUID = shop.getUniqueId();
            removeUUIDFromFile(shopUUID);
            player.sendMessage("Shop UUID erfolgreich aus der Datei entfernt.");
        } else {
            player.sendMessage("Du schaust nicht auf einen Shop.");
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Shop shop = getTargetShop(player);
        if (shop != null) {
            player.sendMessage("Shop UUID: " + shop.getUniqueId());
        }
    }

    private Shop getTargetShop(Player player) {
        // Implementiere Logik zum Bestimmen, auf welchen Shop der Spieler schaut
        // Beispiel: ShopManager.getInstance().getShopAtLocation(player.getTargetBlock(null, 5).getLocation());
        return null;
    }

    private void writeUUIDToFile(UUID shopUUID) {
        File file = new File(plugin.getDataFolder(), "shop_uuids.json");

        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader);
             FileWriter writer = new FileWriter(file)) {

            JSONArray jsonArray;

            try {
                // Versuche, die vorhandenen Daten aus der Datei zu lesen
                jsonArray = new JSONArray(bufferedReader.readLine());
            } catch (JSONException | IOException e) {
                // Wenn die Datei leer oder ungültig ist, erstelle eine neue JSON-Array
                jsonArray = new JSONArray();
            }

            // Überprüfe, ob die UUID bereits in der Liste ist
            if (!isUUIDInList(shopUUID, jsonArray)) {
                // Füge die neue Shop-UUID hinzu
                jsonArray.put(new JSONObject().put("shop_uuid", shopUUID.toString()));
                // Schreibe das aktualisierte JSON-Array zurück in die Datei
                writer.write(jsonArray.toString(2));
            } else {
                // Sende eine Nachricht an den Spieler, dass der Shop bereits in der Liste ist
                // Du kannst diese Nachricht nach deinen Bedürfnissen anpassen
                // In diesem Beispiel wird angenommen, dass der Spieler `player` eine Instanz von Player ist
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    player.sendMessage("Dieser Shop ist bereits versteckt.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hilfsmethode zur Überprüfung, ob die UUID bereits in der Liste ist
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
                // Versuche, die vorhandenen Daten aus der Datei zu lesen
                jsonArray = new JSONArray(bufferedReader.readLine());
            } catch (JSONException | IOException e) {
                // Wenn die Datei leer oder ungültig ist, gibt es nichts zu entfernen
                return;
            }

            // Durchsuche das JSON-Array nach der entsprechenden Shop-UUID und entferne sie
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String existingUUID = jsonObject.getString("shop_uuid");
                if (existingUUID.equals(shopUUID.toString())) {
                    jsonArray.remove(i);
                    break; // Sobald die UUID gefunden und entfernt wurde, breche die Schleife ab
                }
            }

            // Schreibe das aktualisierte JSON-Array zurück in die Datei
            writer.write(jsonArray.toString(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
