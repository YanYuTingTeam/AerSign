package com.aermini.sign.nms.v1_20_R4;

import com.aermini.sign.nms.SignNms;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class SignNmsImpl implements SignNms {

    @Override
    public String versionName() {
        return "v1_20_R4~26.2";
    }

    @Override
    public Enchantment glowEnchant() {
        return Enchantment.UNBREAKING;
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
