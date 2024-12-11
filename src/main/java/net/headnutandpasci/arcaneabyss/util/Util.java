package net.headnutandpasci.arcaneabyss.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.function.Predicate;

public class Util {

    public static void pushPlayer(LivingEntity attacker, PlayerEntity target, float baseDamage, double powah) {
        if (target.isBlocking()) {
            target.disableShield(true);
            baseDamage *= 0.3F;
        }

        Vec3d direction = new Vec3d(target.getX() - attacker.getX(), 0, target.getZ() - attacker.getZ()).normalize();

        target.addVelocity(direction.x * powah, 1, direction.z * powah);
        target.velocityModified = true;
        target.damage(attacker.getDamageSources().mobAttack(attacker), baseDamage);
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
}
