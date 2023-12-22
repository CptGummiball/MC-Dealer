package org.mcdealer.mcdealer.Utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public class ShopHandler implements Listener {

    private final JavaPlugin plugin;

    private static final Logger logger = LoggerFactory.getLogger("MCDealer (ShopHandler)");

    public ShopHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleGetShopUUID(Player player) {
        Translator translator = Translator.getInstance();
        Villager villager = getTargetVillager(player);
        if (villager != null) {
            UUID shopUUID = villager.getUniqueId();
            if (writeUUIDToFile(shopUUID)) {
                player.sendMessage(translator.translate("ShopHandler.notdisplayed"));
            } else {
                player.sendMessage(translator.translate("ShopHandler.alreadyhidden"));
            }
        } else {
            player.sendMessage(translator.translate("ShopHandler.notlooking"));
        }
    }

    public void handleRemoveShopUUID(Player player) {
        Translator translator = Translator.getInstance();
        Villager villager = getTargetVillager(player);
        if (villager != null) {
            UUID shopUUID = villager.getUniqueId();
            boolean removed = removeUUIDFromFile(shopUUID);
            if (removed) {
                player.sendMessage(translator.translate("ShopHandler.isdisplayed"));
            } else {
                player.sendMessage(translator.translate("ShopHandler.alreadydisplayed"));
            }
        } else {
            player.sendMessage(translator.translate("ShopHandler.notlooking"));
        }
    }

    public void handleCheckHiddenShops(Player player) {
        File hiddenShopsFile = new File(plugin.getDataFolder(), "web/hidden_shops.json");
        File outputShopsFile = new File(plugin.getDataFolder(), "web/output.json");
        Translator translator = Translator.getInstance();
        try (FileReader hiddenReader = new FileReader(hiddenShopsFile);
             FileReader outputReader = new FileReader(outputShopsFile)) {

            // Parse JSON arrays from the files
            JSONArray hiddenArray = new JSONArray(new JSONTokener(hiddenReader));
            JSONArray outputArray = new JSONObject(new JSONTokener(outputReader)).getJSONArray("shops");

            // Build the message string
            StringBuilder message = new StringBuilder(translator.translate("ShopHandler.hiddenshops"));

            // Iterate through the arrays and compare UUIDs
            for (int i = 0; i < hiddenArray.length(); i++) {
                String hiddenUUID = hiddenArray.getString(i);

                for (int j = 0; j < outputArray.length(); j++) {
                    String outputUUID = outputArray.getJSONObject(j).getString("shop_uuid");

                    if (hiddenUUID.equals(outputUUID)) {
                        String shopName = getShopNameByUUID(outputArray, outputUUID);
                        message.append(shopName).append(", ");
                        break;
                    }
                }
            }

            // Remove the trailing comma and space if shops were found
            if (message.length() > (translator.translate("ShopHandler.hiddenshops")).length()) {
                message.setLength(message.length() - 2);
            } else {
                // If no shops were found, provide a different message
                message.append(translator.translate("ShopHandler.nohiddenshops"));
            }

            // Send the message
            player.sendMessage(message.toString());

        } catch (IOException | JSONException e) {
            logger.error("An error occurred while checking for hidden shops! ", e);
        }
    }

    private String getShopNameByUUID(JSONArray outputArray, String uuid) {
        for (int i = 0; i < outputArray.length(); i++) {
            JSONObject shopObject = outputArray.getJSONObject(i);
            if (uuid.equals(shopObject.getString("shop_uuid"))) {
                return shopObject.getString("shop_name");
            }
        }
        return null; // Return null if shop name is not found
    }

    public Villager getTargetVillager(Player player) {
        for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
            if (entity instanceof Villager) {
                return (Villager) entity;
            }
        }
        return null;
    }

    private boolean writeUUIDToFile(UUID shopUUID) {
        File file = new File(plugin.getDataFolder(), "web/hidden_shops.json");

        try {
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    // Handle the case where the file creation was not successful
                    logger.error("Failed to create the file: " + file.getPath());
                    return false;
                }
            }

            // Read existing content
            StringBuilder fileContent = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileContent.append(line);
                }
            }

            // Parse existing JSON array or create a new one
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(fileContent.toString());
            } catch (JSONException e) {
                jsonArray = new JSONArray();
            }

            // Check if UUID is already in the list
            if (!isUUIDInList(shopUUID, jsonArray)) {
                jsonArray.put(shopUUID.toString());

                // Write the updated JSON array back to the file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(jsonArray.toString());
                }
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Error writing to file ", e);
            return false;
        }
    }


    private boolean isUUIDInList(UUID uuid, JSONArray jsonArray) {
        // Konvertiere die UUID zu einem String, da die JSON-Liste Strings enthält
        String uuidString = uuid.toString();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);

            if (item instanceof String) {
                // Handle the case where the item is a direct UUID string
                if (uuidString.equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean removeUUIDFromFile(UUID shopUUID) {
        File file = new File(plugin.getDataFolder(), "web/hidden_shops.json");

        try {
            if (!file.exists()) {
                // Wenn die Datei nicht existiert, gibt es nichts zu entfernen
                return false;
            }

            // Read the existing content of the file
            StringBuilder fileContent = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileContent.append(line);
                }
            }

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(fileContent.toString());

            // Verify that the UUID was found and removed
            boolean removed = false;
            Iterator<Object> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                String existingUUID = (String) iterator.next();
                if (existingUUID.equals(shopUUID.toString())) {
                    iterator.remove();
                    removed = true;
                    break;
                }
            }

            // Write the updated JSON array back to the file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonArray.toString(2));
            }

            return removed;

        } catch (IOException | JSONException e) {
            logger.error("Failed to update hidden_shops.json", e);
        }

        return false;
    }

    public static boolean isShopOwner(Player player, Villager villager) {
        // Holen des Datenverzeichnisses des "VillagerMarket"-Plugins
        File dataFolder = new File("plugins/VillagerMarket/Shops");

        // Erstellen des Verzeichnispfads zur YAML-Datei
        File shopFile = new File(dataFolder, villager.getUniqueId().toString() + ".yml");

        // Überprüfen, ob die YAML-Datei existiert
        if (!shopFile.exists()) {
            return false; // Der Shop existiert nicht
        }

        // Laden der YAML-Konfiguration
        YamlConfiguration config = YamlConfiguration.loadConfiguration(shopFile);

        // Überprüfen, ob der Eintrag "type" vorhanden ist und ob es sich um einen Admin-Shop handelt
        if (config.contains("type") && config.getString("type").equalsIgnoreCase("ADMIN")) {
            return player.hasPermission("mcdealer.admin");
        }

        // Überprüfen, ob der Eintrag "ownerUUID" vorhanden ist
        if (!config.contains("ownerUUID")) {
            return false; // Der Eintrag "ownerUUID" fehlt
        }

        // Vergleich der Spieler-UUID mit der in der YAML-Datei gespeicherten UUID
        UUID ownerUUID = UUID.fromString(config.getString("ownerUUID"));
        return player.getUniqueId().equals(ownerUUID) || player.hasPermission("mcdealer.admin");
    }
}