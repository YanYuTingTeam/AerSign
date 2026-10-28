package com.aermini.sign.nms;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public interface SignNms {

    String versionName();

    Enchantment glowEnchant();

    Material material(String name);
}
