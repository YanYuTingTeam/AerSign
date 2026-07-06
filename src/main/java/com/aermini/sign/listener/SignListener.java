package com.aermini.sign.listener;

import com.aermini.sign.AerSign;
import com.aermini.sign.manager.DatabaseManager;
import com.aermini.sign.ui.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SignListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle() == null) return;
        String title = AerSign.color(AerSign.getInstance().getConfig().getString("ui.title"));
        if (!event.getInventory().getTitle().equals(title)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;
        int slot = event.getSlot();
        int day = SignGUI.getDayFromSlot(slot);
        if (day == -1) return;
        DatabaseManager db = AerSign.getInstance().getDatabaseManager();

        String uuid = player.getUniqueId().toString();
        int totalSignCount = db.getTotalSignCount(uuid);
        int h = totalSignCount % 7;
        boolean hasSignedToday = db.hasSignedToday(uuid);

        if (hasSignedToday) return;
        if (day != h + 1) return;
        SignGUI.executeSignCommands(player, day);
        db.incrementSignCount(uuid);
        String dayPath = "day" + day;
        String formatName = AerSign.getInstance().getConfig().getString(dayPath + ".format_name");
        String message = AerSign.getInstance().getConfig().getString("message");
        message = message.replace("{format_name}", formatName);
        player.sendMessage(AerSign.color(message));
        Bukkit.getScheduler().runTaskLater(AerSign.getInstance(), () -> {
            player.closeInventory();
            SignGUI.openGUI(player);
        }, 1L);
    }
}
