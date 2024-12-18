package net.headnutandpasci.arcaneabyss.item.enchantments;

import net.headnutandpasci.arcaneabyss.item.ModEnchantments;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class StickyDefense extends Enchantment {
    private static int particleTimer = 0;

    public StickyDefense() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    public static void tick(LivingEntity wearer) {
        boolean hasStickyDefense = Util.hasEnchantment(ModEnchantments.STICKY_DEFENSE, wearer.getArmorItems());
        if (!hasStickyDefense) return;

        if (wearer.getWorld() instanceof ServerWorld serverWorld) {
            Box detectionBox = wearer.getBoundingBox().expand(3);
            List<HostileEntity> nearbyHostile = serverWorld.getOtherEntities(wearer, detectionBox, entity -> entity instanceof HostileEntity).stream().map(entity -> (HostileEntity) entity).toList();

            if (!nearbyHostile.isEmpty()) {
                //TODO: check for multiple players
                if (--particleTimer <= 0) {
                    spawnParticles(serverWorld, wearer.getPos(), wearer);
                    particleTimer = 10;
                }

                for (HostileEntity hostile : nearbyHostile) {
                    hostile.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5, 1));
                }
            }
        }
    }

    //TODO: check for performance
    private static void spawnParticles(ServerWorld world, Vec3d position, LivingEntity wearer) {
        double radius = 3;
        int particleCount = 30;

        Vec3d to = new Vec3d(position.x, position.y - 5, position.z);
        BlockHitResult hitResult = world.raycast(new RaycastContext(
                position,
                to,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                wearer
        ));

        double groundY = hitResult.getType() == BlockHitResult.Type.BLOCK
                ? hitResult.getPos().y
                : position.y;

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);

            world.spawnParticles(
                    ParticleTypes.CRIT,
                    position.x + offsetX, groundY + 0.1, position.z + offsetZ,
                    1,
                    0, 0, 0,
                    0.1
            );
        }
    }

    @Override
    public int getMinPower(int level) {
        return 10;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }
}
