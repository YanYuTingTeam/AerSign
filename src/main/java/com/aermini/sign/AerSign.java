package com.aermini.sign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.aermini.sign.command.SignCommand;
import com.aermini.sign.listener.SignListener;
import com.aermini.sign.manager.DatabaseManager;

public final class AerSign extends JavaPlugin {
    private static AerSign instance;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        databaseManager = new DatabaseManager();
        databaseManager.initialize();
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
