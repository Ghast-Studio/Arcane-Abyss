package net.headnutandpasci.arcaneabyss.item.custom.ring;

import dev.emi.trinkets.api.TrinketItem;
import net.headnutandpasci.arcaneabyss.util.interfaces.RingAttackable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DefenseRing extends TrinketItem implements RingAttackable {
    public DefenseRing(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void onPlayerAttacked(PlayerEntity player, DamageSource source) {
        if (!player.world.isClient && player.getInventory().contains(new ItemStack(this))) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 0, false, true));
        }
    }
}
