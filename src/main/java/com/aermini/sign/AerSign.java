package com.aermini.sign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.aermini.sign.command.SignCommand;
import com.aermini.sign.listener.SignListener;
import com.aermini.sign.manager.DatabaseManager;
import com.aermini.sign.ui.VexViewGUI;

public final class AerSign extends JavaPlugin {
    private static AerSign instance;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        databaseManager = new DatabaseManager();
        databaseManager.initialize();
        VexViewGUI.loadConfig(this);
        if (Bukkit.getPluginManager().isPluginEnabled("VexView") && VexViewGUI.isEnabled()) {
            getLogger().info("检测到VexView，已启用自定义UI");
        }
        getCommand("aersign").setExecutor(new SignCommand());
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        getLogger().info("AerSign 已启用");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        getLogger().info("AerSign 已禁用");
    }

    public static AerSign getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
