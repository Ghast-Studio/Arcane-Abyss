package net.headnutandpasci.arcaneabyss.item.custom.chest;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;

import java.util.List;

public class DefenseNecklace extends TrinketItem {
    ParticleEffect particleEffect = new DustParticleEffect(new Vector3f(0, 1000, 0), 0.5f);

    public DefenseNecklace(Settings settings) {
        super(settings);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity wearer))
            return;

        Box detectionBox = wearer.getBoundingBox().expand(3);
        List<HostileEntity> nearbyHostile = wearer.getWorld().getEntitiesByClass(HostileEntity.class, detectionBox, HostileEntity::isAlive);

        if (!nearbyHostile.isEmpty()) {
            int spawnedParticles = 0;

            for (HostileEntity hostile : nearbyHostile) {
                if (++spawnedParticles < 5)
                    Util.spawnCircleParticles(hostile.getWorld(), hostile.getPos(), particleEffect, 1, 20, true);

                hostile.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5, 1));
            }
        }
    }
}
