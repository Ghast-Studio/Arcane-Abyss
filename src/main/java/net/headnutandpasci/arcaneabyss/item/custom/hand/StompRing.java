package net.headnutandpasci.arcaneabyss.item.custom.hand;

import dev.emi.trinkets.api.TrinketItem;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.headnutandpasci.arcaneabyss.util.interfaces.RingAttackable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;

public class StompRing extends TrinketItem implements RingAttackable {

    private final Set<MobEntity> attackers = new HashSet<>();
    public StompRing(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlayerAttacked(PlayerEntity player, DamageSource source) {
        if (source.getAttacker() instanceof MobEntity mob) {

            if (mob instanceof BlackSlimeEntity || mob instanceof SlimeviathanEntity) {
                return;
            }
            attackers.add(mob);
        }
        player.world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(3), attackers::contains)
                .forEach(mob -> Util.pushEntityAwayFrom(player, mob, 2, 0.5));


        attackers.removeIf(mob -> !mob.isAlive());
    }
}
