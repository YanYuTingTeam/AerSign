package com.aermini.sign.nms.v1_8_R1;

import com.aermini.sign.nms.SignNms;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class SignNmsImpl implements SignNms {

    @Override
    public String versionName() {
        return "v1_8_R1~v1_12_R2";
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
        return Material.matchMaterial(name);
    }
}
