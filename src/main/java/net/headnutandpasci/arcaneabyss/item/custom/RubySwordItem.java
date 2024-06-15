package net.headnutandpasci.arcaneabyss.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.headnutandpasci.arcaneabyss.item.ModToolMaterial;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RubySwordItem extends SwordItem {
    public RubySwordItem(Settings settings){
        super(ModToolMaterial.RUBY,4, 0.5F, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_sword.tooltip"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
