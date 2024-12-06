package net.headnutandpasci.arcaneabyss.item.enchantments;

import net.headnutandpasci.arcaneabyss.item.ModEnchantments;
import net.headnutandpasci.arcaneabyss.item.custom.RubySwordItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class SlimyStrike extends Enchantment {
    public static final String STACK_KEY = "SlimyStrikeStack";
    public static final String ID = "slimy_strike";

    public SlimyStrike() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    private static DamageSource createPlayerDamageSource(PlayerEntity player, DamageSources damageSources) {
        return damageSources.playerAttack(player);
    }

    public static int calculateRequiredStacks(int level) {
        return switch (level) {
            case 2 -> 15;
            case 3 -> 10;
            default -> 20;
        };
    }

    @Override
    public int getMinPower(int level) {
        return 10 + level * 5;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (user instanceof PlayerEntity player && target instanceof LivingEntity livingTarget) {
            ItemStack weapon = player.getMainHandStack();
            if (EnchantmentHelper.get(weapon).containsKey(ModEnchantments.SLIME_ENCHANTMENT)) {
                NbtCompound nbt = weapon.getOrCreateNbt();
                int stacks = nbt.getInt(STACK_KEY) + 1;
                nbt.putInt(STACK_KEY, stacks);

                if (stacks >= calculateRequiredStacks(level) * 2) {
                    nbt.putInt(STACK_KEY, 0);
                    float bonusDamage = 5 + (level * 2);

                    if (weapon.getItem() instanceof RubySwordItem sword) {
                        bonusDamage += sword.getUpgradeLevel(weapon) * 2;
                    }

                    livingTarget.damage(createPlayerDamageSource(player, player.getDamageSources()), bonusDamage);

                    if (player.getWorld() instanceof ServerWorld world) {
                        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SLIME_SQUISH, player.getSoundCategory(), 2.0F, 1.0F);
                        world.spawnParticles(ParticleTypes.ITEM_SLIME, livingTarget.getX(), livingTarget.getY(), livingTarget.getZ(), 10, 0.5, 0.5, 0.5, 0.3);
                    }
                }
            }
        }

        super.onTargetDamaged(user, target, level);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }
}
