package com.aermini.sign.ui;

import com.aermini.sign.AerSign;
import com.aermini.sign.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignGUI {
    private static final Map<Integer, Integer> SLOT_MAPPING = Map.of(
        1, 10,
        2, 11,
        3, 12,
        4, 13,
        5, 14,
        6, 15,
        7, 16
    );

    public static void openGUI(Player player) {
        String uuid = player.getUniqueId().toString();
        DatabaseManager db = AerSign.getInstance().getDatabaseManager();
        if (!db.hasPlayerData(uuid)) {
            db.createPlayerData(uuid);
        }
        int slotSize = AerSign.getInstance().getConfig().getInt("ui.slot");
        String title = AerSign.color(AerSign.getInstance().getConfig().getString("ui.title"));
        Inventory inventory = Bukkit.createInventory(null, slotSize, title);
        int totalSignCount = db.getTotalSignCount(uuid);
        int h = totalSignCount % 7;
        boolean hasSignedToday = db.hasSignedToday(uuid);

        for (int day = 1; day <= 7; day++) {
            int slot = SLOT_MAPPING.getOrDefault(day, 10 + (day - 1));
            ItemStack item = createDayItem(player, day, h, hasSignedToday);
            if (item != null) {
                inventory.setItem(slot, item);
            }
        }
        player.openInventory(inventory);
    }

    private static ItemStack createDayItem(Player player, int day, int h, boolean hasSignedToday) {
        String type;
        if (day <= h) {
            type = "claimed";
        } else if (day == h + 1 && !hasSignedToday) {
            type = "can_claim";
        } else if (h == 0 && hasSignedToday) {
            type = "claimed";
        } else {
            type = "cant_claim";
        }
        String materialName = AerSign.getInstance().getConfig().getString("ui.items." + type + ".item");
        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            material = Material.PAPER;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String namePath = "ui.items." + type + ".name";
        String name = AerSign.getInstance().getConfig().getString(namePath);
        if (name != null) {
            name = name.replace("{day}", String.valueOf(day));
            name = replaceDayVariables(name, day);
            meta.setDisplayName(AerSign.color(name));
        }
        List<String> loreList = AerSign.getInstance().getConfig().getStringList("ui.items." + type + ".lore");
        List<String> coloredLore = new ArrayList<>();
        for (String line : loreList) {
            line = line.replace("{day}", String.valueOf(day));
            line = replaceDayVariables(line, day);
            coloredLore.add(AerSign.color(line));
        }
        meta.setLore(coloredLore);
        boolean glow = AerSign.getInstance().getConfig().getBoolean("ui.items." + type + ".glow");
        if (glow) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
        }

        item.setItemMeta(meta);
        return item;
    }

    private static String replaceDayVariables(String text, int day) {
        String dayPath = "day" + day;
        if (AerSign.getInstance().getConfig().contains(dayPath + ".reward_name")) {
            String rewardName = AerSign.getInstance().getConfig().getString(dayPath + ".reward_name");
            text = text.replace("{reward_name}", rewardName);
        }
        if (AerSign.getInstance().getConfig().contains(dayPath + ".name")) {
            String name = AerSign.getInstance().getConfig().getString(dayPath + ".name");
            text = text.replace("{reward_name}", name);
        }
        return text;
    }

    public static int getDayFromSlot(int slot) {
        for (Map.Entry<Integer, Integer> entry : SLOT_MAPPING.entrySet()) {
            if (entry.getValue() == slot) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
