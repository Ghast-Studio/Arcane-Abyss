package net.headnutandpasci.arcaneabyss.item.custom;

import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StompRing extends Item {

    public StompRing(Settings settings) {
        super(settings);
    }

    public void onPlayerDamaged(PlayerEntity player) {
        long ringCount = player.getInventory().main.stream()
                .filter(stack -> !stack.isEmpty() && stack.getItem() == ModItems.BULWARK_STOMP_RING)
                .count();
        if (!player.world.isClient) {
            player.world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(5), mob -> true)
                    .forEach(mob -> pushEntityAway(player, mob, ringCount));
        }
    }

    private void pushEntityAway(PlayerEntity player, LivingEntity mob, long ringCount) {
        World world = player.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            double x = player.getX();
            double y = player.getY() + 1.0;
            double z = player.getZ();

            // Play sound safely
            serverWorld.playSound(null, x, y, z,

                    SoundEvents.ITEM_SHIELD_BLOCK,
                    player.getSoundCategory(), 1F, 1.0F);

            // Prevent division by zero or invalid values
            double knockbackStrength = 2.0 / Math.max(ringCount, 3); // Clamp division logic
            Vec3d knockbackDirection = mob.getPos().subtract(player.getPos()).normalize().multiply(knockbackStrength);

            for (int i = 0; i < 5; i++) {
                serverWorld.spawnParticles(
                        ParticleTypes.POOF,
                        x + Math.random() * 0.5 - 0.25,
                        y + Math.random() * 0.5 - 0.25,
                        z + Math.random() * 0.5 - 0.25,
                        1,
                        0.0, 0.0, 0.0,
                        1.0
                );
            }

            // Safeguard mob velocity to avoid invalid physics
            mob.addVelocity(knockbackDirection.x, 0.3899, knockbackDirection.z);
            mob.velocityModified = true;
        }
    }
}
