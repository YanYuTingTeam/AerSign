package com.aermini.sign.nms;

import org.bukkit.Bukkit;

public final class SignNmsProvider {

    private static SignNms instance;

    private SignNmsProvider() {
    }

    public static SignNms get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static SignNms load() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        String key = pickVersion(bukkitVersion);
        try {
            Class<?> clazz = Class.forName(SignNmsProvider.class.getPackage().getName() + "." + key + ".SignNmsImpl");
            return (SignNms) clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            Bukkit.getLogger().warning("AerSign 未能加载 " + key + " 版本实现(" + bukkitVersion + "), 已回退通用实现: " + e);
            return new CommonSignNms();
        }
    }

    static String pickVersion(String bukkitVersion) {
        String v = bukkitVersion == null ? "" : bukkitVersion;
        int dash = v.indexOf('-');
        if (dash > 0) {
            v = v.substring(0, dash);
        }
        String[] parts = v.split("\\.");
        try {
            int major = Integer.parseInt(parts[0]);
            if (major != 1) {
                return "v1_20_R4";
            }
            int minor = Integer.parseInt(parts[1]);
            if (minor <= 12) {
                return "v1_8_R1";
            }
            if (minor <= 19) {
                return "v1_13_R1";
            }
            if (minor == 20) {
                int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                return patch >= 5 ? "v1_20_R4" : "v1_13_R1";
            }
            return "v1_20_R4";
        } catch (RuntimeException e) {
            return "v1_20_R4";
        }
    }
}
