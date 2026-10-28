package com.aermini.sign.nms;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

class CommonSignNms implements SignNms {

    @Override
    public String versionName() {
        return "common";
    }

    @Override
    public Enchantment glowEnchant() {
        return null;
    }

    @Override
    public Material material(String name) {
        if (name == null) {
            return null;
        }
        return Material.matchMaterial(name);
    }
}
