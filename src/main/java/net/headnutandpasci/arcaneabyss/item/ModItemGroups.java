package net.headnutandpasci.arcaneabyss.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup SLIME_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(ArcaneAbyss.MOD_ID, "slime_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.slime"))
                    .icon(() -> new ItemStack(Items.SLIME_BALL)).entries((displayContext, entries) -> {
                        entries.add(ModItems.SLIMESTEEL_INGOT);
                        entries.add(ModItems.OBSIDIANSTEEL_INGOT);
                        entries.add(ModItems.SLIME_JUICE);
                        entries.add(ModItems.SLIME_CRYSTALLISATION);
                        entries.add(ModItems.SLIME_FLESH);
                        entries.add(ModItems.COOKED_SLIME_MEAT);

                        entries.add(ModBlocks.SLIMESTEEL_MACHINE);

                        entries.add(ModItems.SLIME_SWORD);
                        entries.add(ModItems.SLIME_STAFF);
                        entries.add(ModItems.DEFENSE_RING);
                        entries.add(ModItems.STOMP_RING);
                        entries.add(ModItems.BULWARK_STOMP_RING);
                        entries.add(ModItems.TELEPORT_RING);

                        //entries.add(ModItems.RUBY_HELMET);
                        //entries.add(ModItems.RUBY_CHESTPLATE);
                        //entries.add(ModItems.RUBY_LEGGINGS);
                        //entries.add(ModItems.RUBY_BOOTS);
                    }).build());

    public static final ItemGroup DUNGEON_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(ArcaneAbyss.MOD_ID, "dungeon"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.dungeon"))
                    .icon(() -> new ItemStack(Items.NETHER_STAR)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.DUNGEON_WALL_BLOCK);
                        entries.add(ModBlocks.DUNGEON_BRICK);
                        entries.add(ModBlocks.DUNGEON_BRICK_CRACKED);
                        entries.add(ModBlocks.DUNGEON_BRICK_SLAB);
                        entries.add(ModBlocks.DUNGEON_BRICK_STAIR);
                        entries.add(ModBlocks.DUNGEON_STONE);
                        entries.add(ModBlocks.DUNGEON_STONE_CHISELED);
                        entries.add(ModBlocks.DUNGEON_COBBLE);
                        entries.add(ModBlocks.DUNGEON_COBBLE_SLAB);
                        entries.add(ModBlocks.DUNGEON_COBBLE_STAIR);
                        entries.add(ModBlocks.DUNGEON_BRICK_WALL);
                    }).build());

    public static void registerItemGroups() {
        ArcaneAbyss.LOGGER.info("Registering Item Groups for" + ArcaneAbyss.MOD_ID);
    }
}
