package net.headnutandpasci.arcaneabyss.item.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;

public class SlimyStrike extends Enchantment {
    private static final String STACKS_KEY = "SlimyStrikeStacks";
    private static int hits = 0;


    public SlimyStrike() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
            player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 0.5F, 1.0F);
            if (weapon.hasEnchantments() && weapon.getEnchantments().toString().contains("arcaneabyss:slimy_strike")) {
                NbtCompound nbt = weapon.getOrCreateNbt();
                int stacks = nbt.getInt(STACKS_KEY);
                hits++;
                if (stacks < 10) {
                    stacks++;
                    nbt.putInt(STACKS_KEY, stacks);
                }else if (hits >= 30) {
                    hits = 0;
                    nbt.putInt(STACKS_KEY, 0);
                }


                float bonusDamage = (float) ((stacks * level) * 0.40);
                System.out.println(bonusDamage);
                livingTarget.damage(createPlayerDamageSource(player, player.getDamageSources()), bonusDamage);
            }
        }

        super.onTargetDamaged(user, target, level);
    }


    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    private static DamageSource createPlayerDamageSource(PlayerEntity player, DamageSources damageSources) {
        return damageSources.playerAttack(player);
    }
}
