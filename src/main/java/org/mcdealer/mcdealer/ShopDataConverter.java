package org.mcdealer.mcdealer;

import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ShopDataConverter {

    private static Long LATEST_FILEMODDATE = null;
    private static Map<String, Double> BEST_OFFERS = new HashMap<>();
    private static final Map<String, Double> BEST_DEMANDS = new HashMap<>();

    private static String cleanMinecraftString(String text) {
        // Pattern for Minecraft formatting codes
        Pattern pattern = Pattern.compile("§[0-9a-fklmnor]");
        return pattern.matcher(text).replaceAll("");
    }

    private static Map<String, Map<String, Object>> readYamlFiles(String directory) {
        Map<String, Map<String, Object>> dataDict = new HashMap<>();
        for (File file : new File(directory).listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                try (InputStream input = new FileInputStream(file)) {
                    Yaml yaml = new Yaml();
                    Map<String, Object> data = yaml.load(input);
                    String baseFilename = file.getName().replaceFirst("[.][^.]+$", "");
                    String uuid = (String) data.getOrDefault("ownerUUID", baseFilename);
                    data.put("shop_uuid", baseFilename);
                    dataDict.put(baseFilename, data);

                    long fileModDate = file.lastModified();
                    if (LATEST_FILEMODDATE == null || fileModDate > LATEST_FILEMODDATE) {
                        LATEST_FILEMODDATE = fileModDate;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataDict;
    }

    public static void main(String[] args) {
        try {
            String DIRECTORY_PATH = "./plugins/VillagerMarket/Shops/";
            Map<String, Map<String, Object>> resultDict = readYamlFiles(DIRECTORY_PATH);

            Map<String, Object> playerShops = new HashMap<>();
            List<Map<String, Object>> shopsList = new ArrayList<>();

            for (String shop : resultDict.keySet()) {
                Map<String, Object> playerShop = new HashMap<>();
                playerShop.put("shop_uuid", resultDict.get(shop).get("shop_uuid"));
                playerShop.put("shop_type", resultDict.get(shop).get("type"));

                if (resultDict.get(shop).containsKey("ownerUUID")) {
                    playerShop.put("owner_uuid", resultDict.get(shop).get("ownerUUID"));
                }

                if (playerShop.get("shop_type").equals("ADMIN")) {
                    playerShop.put("owner_name", "ADMIN");
                }

                if (resultDict.get(shop).containsKey("ownerName")) {
                    playerShop.put("owner_name", resultDict.get(shop).get("ownerName"));
                }

                playerShop.put("shop_name", cleanMinecraftString((String) resultDict.get(shop).get("entity.name")));
                playerShop.put("shop_name", cleanMinecraftString(playerShop.get("shop_name").toString().replaceAll("\\[.*?]", "").trim()));
                playerShop.put("npc_profession", resultDict.get(shop).get("entity.profession"));

                Map<String, Object> location = new HashMap<>();
                location.put("world", resultDict.get(shop).get("entity.location.world"));
                location.put("x", resultDict.get(shop).get("entity.location.x"));
                location.put("y", resultDict.get(shop).get("entity.location.y"));
                location.put("z", resultDict.get(shop).get("entity.location.z"));
                playerShop.put("location", location);

                Map<String, List<Map<String, Object>>> playerOffers = new HashMap<>();
                Map<String, List<Map<String, Object>>> playerDemands = new HashMap<>();

                if (resultDict.get(shop).containsKey("items_for_sale")) {
                    List<Map<String, Object>> itemsForSale = (List<Map<String, Object>>) resultDict.get(shop).get("items_for_sale");
                    List<Map<String, Object>> offersList = new ArrayList<>();
                    for (Map<String, Object> offer : itemsForSale) {
                        Map<String, Object> playerOffer = new HashMap<>();

                        // Conversion logic for offers
                        playerOffer.put("own_name", null);
                        String item_type = (String) offer.get("item.type");
                        String item_index = item_type;

                        if (item_type.equals("POTION")) {
                            if (offer.containsKey("item.meta.potion-type")) {
                                item_type = (String) offer.get("item.meta.potion-type");
                                item_index = item_type;
                            } else {
                                String own_name = (String) offer.get("item.meta.display-name");
                                item_index = own_name;
                            }
                        } else if (item_type.equals("ENCHANTED_BOOK")) {
                            item_type = "ENCHANTED_BOOK_" + ((List<String>) offer.get("item.meta.stored-enchants")).get(0);
                            item_index = item_type;
                        }

                        if (offer.containsKey("item.meta.display-name")) {
                            String jsonDisplayName = (String) offer.get("item.meta.display-name");
                            // Parse JSON display name if needed
                            // ...
                        }

                        playerOffer.put("item", item_type.replace("minecraft:", ""));
                        playerOffer.put("amount", offer.get("amount"));
                        playerOffer.put("exchange_item", "money");

                        if (offer.containsKey("price.type")) {
                            playerOffer.put("exchange_item", ((String) offer.get("price.type")).replace("minecraft:", ""));
                            playerOffer.put("price", 1);
                            if (offer.containsKey("price.amount")) {
                                playerOffer.put("price", offer.get("price.amount"));
                            }
                        }

                        playerOffer.put("price_discount", 0);
                        if (offer.containsKey("discount.amount")) {
                            playerOffer.put("price_discount", offer.get("discount.amount"));
                        }

                        playerOffer.put("unit_price", ((Double) playerOffer.get("price")) / ((Integer) playerOffer.get("amount")));
                        playerOffer.put("stock", 0);
                        playerOffer.put("is_best_price", null);

                        if (offer.containsKey("item.meta.enchants")) {
                            List<Map<String, Object>> enchantsList = (List<Map<String, Object>>) offer.get("item.meta.enchants");
                            List<Map<String, Object>> enchants = new ArrayList<>();
                            for (Map<String, Object> enchant : enchantsList) {
                                Map<String, Object> enchantData = new HashMap<>();
                                enchantData.put("name", enchant.get("name"));
                                enchantData.put("level", enchant.get("level"));
                                enchants.add(enchantData);
                            }
                            playerOffer.put("enchants", enchants);
                        }

                        if (playerShop.get("shop_type").equals("ADMIN") && playerOffer.get("exchange_item").equals("money")) {
                            double discountedUnitPrice = ((Double) playerOffer.get("unit_price")) * (1 - (((Double) playerOffer.get("price_discount")) / 100));
                            if (!BEST_OFFERS.containsKey(playerOffer.get("item")) || BEST_OFFERS.get(playerOffer.get("item")) > discountedUnitPrice) {
                                BEST_OFFERS.put((String) playerOffer.get("item"), discountedUnitPrice);
                            }
                        }

                        offersList.add(playerOffer);
                    }
                    playerOffers.put("offers", offersList);
                }

                if (resultDict.get(shop).containsKey("storage")) {
                    List<Map<String, Object>> storage = (List<Map<String, Object>>) resultDict.get(shop).get("storage");
                    Map<String, Integer> playerStocks = new HashMap<>();
                    for (Map<String, Object> stock : storage) {
                        String item_type = (String) stock.get("type");
                        String item_index = item_type;

                        if (item_type.equals("POTION")) {
                            if (stock.containsKey("meta.potion-type")) {
                                item_type = (String) stock.get("meta.potion-type");
                                item_index = item_type;
                            } else {
                                String own_name = (String) stock.get("item.display-name");
                                item_index = own_name;
                            }
                        } else if (item_type.equals("ENCHANTED_BOOK")) {
                            item_type = "ENCHANTED_BOOK_" + ((List<String>) stock.get("meta.stored-enchants")).get(0);
                            item_index = item_type;
                        }

                        if (stock.containsKey("meta.display-name")) {
                            String jsonDisplayName = (String) stock.get("meta.display-name");
                            // Parse JSON display name if needed
                            // ...
                        }

                        int myAmount = stock.containsKey("amount") ? (Integer) stock.get("amount") : 1;

                        playerStocks.put(item_index, playerStocks.getOrDefault(item_index, 0) + myAmount);
                    }

                    // Transfer stock levels to offers and demands
                    for (String stockKey : playerStocks.keySet()) {
                        if (playerOffers.containsKey(stockKey)) {
                            String bestOffersKey = (String) playerOffers.get(stockKey).get(0).get("item");
                            playerOffers.get(stockKey).get(0).put("stock", playerStocks.get(stockKey));
                            double discountedUnitPrice = ((Double) playerOffers.get(stockKey).get(0).get("unit_price")) * (1 - (((Double) playerOffers.get(stockKey).get(0).get("price_discount")) / 100));

                            if (playerOffers.get(stockKey).get(0).get("exchange_item").equals("money") &&
                                    playerStocks.get(stockKey) > 0 &&
                                    (!BEST_OFFERS.containsKey(bestOffersKey) || BEST_OFFERS.get(bestOffersKey) > discountedUnitPrice)) {
                                BEST_OFFERS.put(bestOffersKey, discountedUnitPrice);
                            }
                        }

                        if (playerDemands.containsKey(stockKey) && playerDemands.get(stockKey).get(0).containsKey("buy_limit") && playerShop.get("shop_type").equals("PLAYER")) {
                            int buyLimit = (Integer) playerDemands.get(stockKey).get(0).get("buy_limit");
                            playerDemands.get(stockKey).get(0).put("buy_limit", buyLimit - playerStocks.get(stockKey));
                            if ((Integer) playerDemands.get(stockKey).get(0).get("buy_limit") < 0) {
                                playerDemands.get(stockKey).get(0).put("buy_limit", 0);
                            }
                        }
                    }
                }

                playerShop.put("offers", playerOffers);
                playerShop.put("demands", playerDemands);
                shopsList.add(playerShop);
            }

            playerShops.put("meta", Map.of("latestfilemoddate", LATEST_FILEMODDATE, "latestfilemoddate_formatted", null));
            playerShops.put("shops", shopsList);

            // Data output as JSON file
            try (Writer writer = new FileWriter("/web/output.json")) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(writer, playerShops);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}