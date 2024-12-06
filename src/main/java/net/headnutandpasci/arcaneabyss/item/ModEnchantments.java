package net.headnutandpasci.arcaneabyss.item;

import net.headnutandpasci.arcaneabyss.item.enchantments.SlimyStrike;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final Enchantment SLIME_ENCHANTMENT = new SlimyStrike();

    public static void registerEnchantments() {
        Registry.register(Registries.ENCHANTMENT, new Identifier("arcaneabyss", "slimy_strike"), SLIME_ENCHANTMENT);
    }
}