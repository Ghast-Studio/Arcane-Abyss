package net.headnutandpasci.arcaneabyss.util;

import net.headnutandpasci.arcaneabyss.components.ModComponents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.function.Predicate;

public class Util {

    public static void pushEntityAwayFrom(LivingEntity attacker, LivingEntity target, float baseDamage, double powah) {
        if (target instanceof PlayerEntity player && player.isBlocking()) {
            player.disableShield(true);
            baseDamage *= 0.3F;
            powah *= 0.7F;
        }

        Vec3d direction = new Vec3d(target.getX() - attacker.getX(), 0, target.getZ() - attacker.getZ()).normalize();

        target.addVelocity(direction.x * powah, 1, direction.z * powah);
        target.velocityModified = true;
        target.damage(attacker.getDamageSources().mobAttack(attacker), baseDamage);

        if (target.getWorld() instanceof ServerWorld world) {
            for (int i = 0; i < 50; i++) {
                double offsetX = (Math.random() - 0.5) * 2.0;
                double offsetY = Math.random() * 2.0;
                double offsetZ = (Math.random() - 0.5) * 2.0;
                world.spawnParticles(ParticleTypes.END_ROD, target.getX() + offsetX, target.getY() + offsetY, target.getZ() + offsetZ, 3, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    public static void spawnCircleParticles(ServerWorld world, Vec3d center, ParticleEffect particle, double radius, int particleCount, boolean spawnOnGround) {
        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            double y = spawnOnGround ? world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) x, (int) z) : centerY;

            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public static Predicate<Entity> visibleTo(LivingEntity entity) {
        return (target) -> {
            if (target == entity) {
                return false;
            }
            if (target instanceof LivingEntity livingEntity) {
                return livingEntity.isAlive() && entity.canSee(livingEntity);
            }
            return false;
        };
    }

    public static Text getStatusEffectDescription(StatusEffectInstance statusEffect, Text spacer, Formatting... formattings) {
        MutableText mutableText = statusEffect.getEffectType().getName().copy();
        if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
            MutableText text = mutableText.append(ScreenTexts.SPACE);
            int amplifier = statusEffect.getAmplifier();
            text.append(Text.translatable("enchantment.level." + (amplifier + 1)));
        }

        return mutableText
                .append(ScreenTexts.SPACE)
                .append(spacer)
                .append(ScreenTexts.SPACE)
                .append(StatusEffectUtil.getDurationText(statusEffect, 1.0F)).formatted(formattings);
    }

    public static boolean hasEnchantment(Enchantment enchantment, Iterable<ItemStack> armorItems) {
        for (ItemStack armor : armorItems) {
            if (EnchantmentHelper.getLevel(enchantment, armor) > 0) {
                return true;
            }
        }
        return false;
    }

    public static void spawnVerticalCircularParticlesFacingPlayer(ServerWorld world, double centerX, double centerY, double centerZ, LivingEntity target, DefaultParticleType particleType, double radius) {
        int particleCount = 20;

        Vec3d directionToPlayer = target.getPos().subtract(centerX, centerY, centerZ).normalize();
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = directionToPlayer.crossProduct(up).normalize();
        Vec3d vertical = right.crossProduct(directionToPlayer).normalize();

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double offsetX = radius * (Math.cos(angle) * right.x + Math.sin(angle) * vertical.x);
            double offsetY = radius * (Math.cos(angle) * right.y + Math.sin(angle) * vertical.y);
            double offsetZ = radius * (Math.cos(angle) * right.z + Math.sin(angle) * vertical.z);

            world.spawnParticles(
                    particleType,
                    centerX + offsetX,
                    centerY + offsetY,
                    centerZ + offsetZ,
                    1,
                    0, 0, 0, 0
            );
        }
    }

    public static void spawnDungeonXp(ServerWorld world, Vec3d pos, int amount) {
        while (amount > 0) {
            int i = ExperienceOrbEntity.roundToOrbSize(amount);
            amount -= i;
            if (!ExperienceOrbEntity.wasMergedIntoExistingOrb(world, pos, i)) {
                ExperienceOrbEntity experienceOrbEntity = new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), i);
                world.spawnEntity(experienceOrbEntity);
                experienceOrbEntity.getComponent(ModComponents.DUNGEON_EXPERIENCE_COMPONENT).setDungeonExperience(true);
            }
        }
    }
}
