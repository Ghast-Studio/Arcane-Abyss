package net.headnutandpasci.arcaneabyss.util;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class TrinketUtil {
    private TrinketUtil() {
    }

    public static boolean hasTrinketItemEquipped(PlayerEntity player, Item item) {
        return TrinketsApi.getTrinketComponent(player).map(component -> component.isEquipped(item)).orElse(false);
    }
}
