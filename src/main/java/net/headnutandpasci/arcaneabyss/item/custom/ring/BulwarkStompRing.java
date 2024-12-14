package net.headnutandpasci.arcaneabyss.item.custom.ring;

import dev.emi.trinkets.api.TrinketItem;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.headnutandpasci.arcaneabyss.util.interfaces.RingAttackable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class BulwarkStompRing extends TrinketItem implements RingAttackable {

    public BulwarkStompRing(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlayerAttacked(PlayerEntity player, DamageSource source) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 1, false, true));
        player.world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(5), mob -> true)
                .forEach(mob -> Util.pushEntityAwayFrom(player, mob, 1, 1));
    }
}
