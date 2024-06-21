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
import java.io.*;

public class ModItemGroups {
    public static ItemGroup RUBY_GROUP;
    public static ItemGroup DUNGEON_GROUP;

    public static void registerItemGroups() {
        RUBY_GROUP  = Registry.register(Registries.ITEM_GROUP,
                new Identifier(ArcaneAbyss.MOD_ID,"ruby"),
                FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ruby"))
                        .icon(() -> new ItemStack(Moditems.RUBY)).entries((displayContext, entries) -> {
                            entries.add(Moditems.RAW_RUBY);
                            entries.add(Moditems.RUBY);

                            entries.add(ModBlocks.RUBY_BLOCK);
                            entries.add(ModBlocks.RAW_RUBY_BLOCK);

                            entries.add(Moditems.RUBY_SWORD);
                            entries.add(Moditems.RUBY_PICKAXE);
                            entries.add(Moditems.RUBY_AXE);
                            entries.add(Moditems.RUBY_HOE);
                        }).build());

        DUNGEON_GROUP = Registry.register(Registries.ITEM_GROUP,
                new Identifier(ArcaneAbyss.MOD_ID,"dungeon"),
                FabricItemGroup.builder().displayName(Text.translatable("itemgroup.dungeon"))
                        .icon(() -> new ItemStack(Items.NETHER_STAR)).entries((displayContext, entries) -> {
                            entries.add(ModBlocks.DUNGEON_WALL_BLOCK);
                        }).build());

        ArcaneAbyss.LOGGER.info("Registering Item Groups for" + ArcaneAbyss.MOD_ID);
    }
}
