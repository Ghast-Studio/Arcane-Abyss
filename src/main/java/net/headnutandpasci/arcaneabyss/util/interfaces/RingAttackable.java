package net.headnutandpasci.arcaneabyss.util.interfaces;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public interface RingAttackable {
    void onPlayerAttacked(PlayerEntity player, DamageSource source);
}
