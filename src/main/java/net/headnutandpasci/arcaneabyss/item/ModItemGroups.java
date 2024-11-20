package net.headnutandpasci.arcaneabyss.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup RUBY_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(ArcaneAbyss.MOD_ID,"ruby"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ruby"))
                    .icon(() -> new ItemStack(ModItems.RUBY)).entries((displayContext, entries) -> {

                        entries.add(ModItems.RAW_RUBY);
                        entries.add(ModItems.RUBY);
                        entries.add(ModItems.SLIMESTEEL_INGOT);


                        entries.add(ModItems.TOMATO);


                        entries.add(ModBlocks.RUBY_BLOCK);
                        entries.add(ModBlocks.RAW_RUBY_BLOCK);
                        entries.add(ModBlocks.SLIMESTEEL_MACHINE);


                        entries.add(ModItems.RUBY_SWORD);
                        entries.add(ModItems.RUBY_PICKAXE);
                        entries.add(ModItems.RUBY_AXE);
                        entries.add(ModItems.RUBY_HOE);
                        entries.add(ModItems.RUBY_STAFF);


                        entries.add(ModItems.RUBY_HELMET);
                        entries.add(ModItems.RUBY_CHESTPLATE);
                        entries.add(ModItems.RUBY_LEGGING);
                        entries.add(ModItems.RUBY_BOOTS);

                    }).build());
    public static final ItemGroup DUNGEON_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(ArcaneAbyss.MOD_ID,"dungeon"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.dungeon"))
                    .icon(() -> new ItemStack(Items.NETHER_STAR)).entries((displayContext, entries) -> {

                        entries.add(ModBlocks.DUNGEON_WALL_BLOCK);

                    }).build());
    public static void registerItemGroups() {
        ArcaneAbyss.LOGGER.info("Registering Item Groups for" + ArcaneAbyss.MOD_ID);
    }
}
