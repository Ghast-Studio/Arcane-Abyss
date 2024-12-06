package net.headnutandpasci.arcaneabyss.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.headnutandpasci.arcaneabyss.item.ModEnchantments;
import net.headnutandpasci.arcaneabyss.item.ModToolMaterial;
import net.headnutandpasci.arcaneabyss.item.enchantments.SlimyStrike;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RubySwordItem extends SwordItem {
    public static final int MAX_UPGRADE_LEVEL = 5;
    private static final String NBT_UPGRADE_LEVEL = "upgrade_level";
    private static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("e72fdb02-49b5-47b6-a4a7-ec4a4a0c2f9a");
    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("e3c33f90-3466-4c33-b182-2e4fa6a90b98");


    public RubySwordItem(Settings settings) {
        super(ModToolMaterial.RUBY, 0, -2.4F, settings);
    }

    public void upgradeSword(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        int currentLevel = nbt.getInt(NBT_UPGRADE_LEVEL);

        if (currentLevel < MAX_UPGRADE_LEVEL) {
            nbt.putInt(NBT_UPGRADE_LEVEL, currentLevel + 1);
        }
    }

    public int getUpgradeLevel(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.getInt(NBT_UPGRADE_LEVEL);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(stack, slot));

        if (slot == EquipmentSlot.MAINHAND && stack.getItem() instanceof RubySwordItem && this.getUpgradeLevel(stack) > 0) {
            int upgradeLevel = getUpgradeLevel(stack);

            double additionalDamage = 2 * upgradeLevel;
            double additionalSpeed = 0.1 * upgradeLevel;

            modifiers.put(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(
                            ATTACK_DAMAGE_MODIFIER_UUID,
                            "Slime Sword attack upgrade",
                            additionalDamage,
                            EntityAttributeModifier.Operation.ADDITION
                    )
            );

            modifiers.put(
                    EntityAttributes.GENERIC_ATTACK_SPEED,
                    new EntityAttributeModifier(
                            ATTACK_SPEED_MODIFIER_UUID,
                            "Slime Sword speed upgrade",
                            additionalSpeed,
                            EntityAttributeModifier.Operation.ADDITION
                    )
            );
        }

        return modifiers;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int upgradeLevel = getUpgradeLevel(stack);
        if (upgradeLevel > 0) {
            tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_sword.upgrade_level", upgradeLevel));
        }

        if (stack.getEnchantments().toString().contains(SlimyStrike.ID)) {
            int stacks = stack.getOrCreateNbt().getInt(SlimyStrike.STACK_KEY) / 2;
            int max_stack = SlimyStrike.calculateRequiredStacks(EnchantmentHelper.getLevel(ModEnchantments.SLIME_ENCHANTMENT, stack));
            tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_sword.stacks", stacks, max_stack));

            if (upgradeLevel > 0) {
                tooltip.add(Text.empty());
                tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_sword.slimy_strike", upgradeLevel * 2).formatted(Formatting.GRAY));
            }
        }

        if (stack.hasEnchantments())
            tooltip.add(Text.empty());

        super.appendTooltip(stack, world, tooltip, context);
    }
}
