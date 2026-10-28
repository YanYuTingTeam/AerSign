package com.aermini.sign.nms.v1_13_R1;

import com.aermini.sign.nms.SignNms;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class SignNmsImpl implements SignNms {

    @Override
    public String versionName() {
        return "v1_13_R1~v1_20_R3";
    }

    @Override
    public Enchantment glowEnchant() {
        return Enchantment.DURABILITY;
    }

    @Override
    public Material material(String name) {
        if (name == null) {
            return null;
        }
        if (name.equalsIgnoreCase("EMPTY_MAP")) {
            return Material.matchMaterial("MAP");
        }
        return Material.matchMaterial(name);
    }
}
