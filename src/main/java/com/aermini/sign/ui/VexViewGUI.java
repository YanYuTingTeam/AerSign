package com.aermini.sign.ui;

import com.aermini.sign.AerSign;
import com.aermini.sign.manager.DatabaseManager;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.ButtonFunction;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexImage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VexViewGUI {
    private static FileConfiguration vexConfig;
    private static final Map<Integer, int[]> dayPosMap = new HashMap<>();
    private static int bgW, bgH, bgX, bgY;
    private static int btnW, btnH, btnX, btnY;

    public static void loadConfig(AerSign plugin) {
        File file = new File(plugin.getDataFolder(), "vexview.yml");
        if (!file.exists()) {
            plugin.saveResource("vexview.yml", false);
        }
        vexConfig = YamlConfiguration.loadConfiguration(file);

        int[] bgSize = parseSize(vexConfig.getString("bgs", "360,240,-1,-1"));
        bgW = bgSize[0]; bgH = bgSize[1]; bgX = bgSize[2]; bgY = bgSize[3];

        int[] btnSize = parseSize(vexConfig.getString("btns", "80,20,140,190"));
        btnW = btnSize[0]; btnH = btnSize[1]; btnX = btnSize[2]; btnY = btnSize[3];

        for (int day = 1; day <= 7; day++) {
            String key = "d" + day + "s";
            int[] pos = parseSize(vexConfig.getString(key, "40,40,0,0"));
            dayPosMap.put(day, pos);
        }
    }

    public static boolean isEnabled() {
        return vexConfig != null && vexConfig.getBoolean("enabled", false);
    }

    public static void openGUI(Player player) {
        if (vexConfig == null) return;

        String uuid = player.getUniqueId().toString();
        DatabaseManager db = AerSign.getInstance().getDatabaseManager();
        if (!db.hasPlayerData(uuid)) {
            db.createPlayerData(uuid);
        }

        int totalSignCount = db.getTotalSignCount(uuid);
        int h = totalSignCount % 7;
        boolean hasSignedToday = db.hasSignedToday(uuid);

        List<lk.vexview.gui.components.VexComponents> components = new ArrayList<>();

        // day1~day7 奖励图片
        for (int day = 1; day <= 7; day++) {
            int[] pos = dayPosMap.get(day);
            boolean claimed = (day <= h) || (h == 0 && hasSignedToday);
            String url = claimed ? vexConfig.getString("d" + day + "o") : vexConfig.getString("d" + day);
            components.add(new VexImage(url, pos[2], pos[3], pos[0], pos[1]));
        }

        // 签到按钮
        String btnUrl = hasSignedToday ? vexConfig.getString("btno") : vexConfig.getString("btn");
        ButtonFunction btnFunc = p -> {
            playClickSound(p);
            doSign(p);
        };
        components.add(new VexButton(1, "", btnUrl, btnUrl, btnX, btnY, btnW, btnH, btnFunc));

        VexGui gui = new VexGui(vexConfig.getString("bg"), bgX, bgY, bgW, bgH, components);
        VexViewAPI.openGui(player, gui);
    }

    private static void doSign(Player player) {
        String uuid = player.getUniqueId().toString();
        DatabaseManager db = AerSign.getInstance().getDatabaseManager();
        int totalSignCount = db.getTotalSignCount(uuid);
        int h = totalSignCount % 7;
        boolean hasSignedToday = db.hasSignedToday(uuid);

        if (hasSignedToday) return;
        int day = h + 1;
        if (day < 1 || day > 7) return;

        // 执行签到命令
        SignGUI.executeSignCommands(player, day);
        db.incrementSignCount(uuid);

        // 发送签到消息
        String dayPath = "day" + day;
        String formatName = AerSign.getInstance().getConfig().getString(dayPath + ".format_name");
        String message = AerSign.getInstance().getConfig().getString("message");
        message = message.replace("{format_name}", formatName);
        player.sendMessage(AerSign.color(message));

        // 刷新gui
        Bukkit.getScheduler().runTaskLater(AerSign.getInstance(), () -> openGUI(player), 1L);
    }

    private static void playClickSound(Player player) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static int[] parseSize(String s) {
        String[] parts = s.split(",");
        return new int[]{
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()),
                Integer.parseInt(parts[3].trim())
        };
    }

    public static boolean hasVexView(Player player) {
        try {
            return VexViewAPI.getPlayerClientWindowWidth(player) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}