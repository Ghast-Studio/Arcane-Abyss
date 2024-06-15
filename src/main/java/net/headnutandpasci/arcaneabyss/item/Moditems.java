package net.headnutandpasci.arcaneabyss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class Moditems {
    public static final Item RUBY = registerItem("ruby", new Item(new FabricItemSettings()));
    public static final Item RAW_RUBY = registerItem("raw_ruby", new Item(new FabricItemSettings()));
    public static final Item RUBY_SWORD = registerItem("ruby_sword", new SwordItem(ModToolMaterial.RUBY, 3, 1f, new FabricItemSettings()));
    public static final Item RUBY_PICKAXE = registerItem("ruby_pickaxe", new PickaxeItem(ModToolMaterial.RUBY,1,1,new FabricItemSettings()));
    public static final Item RUBY_AXE = registerItem("ruby_axe", new AxeItem(ModToolMaterial.RUBY,4,1,new FabricItemSettings()));
    public static final Item RUBY_HOE = registerItem("ruby_hoe", new HoeItem(ModToolMaterial.RUBY,1,1,new FabricItemSettings()));

    public static final Item PORCUPINE_SPAWN_EGG = registerItem("porcupine_spawn_egg",
    new SpawnEggItem(ModEntities.PORCUPINE, 0xa86518, 0x3b260f, new FabricItemSettings()));

    public static final Item Cyanslime_SPAWN_EGG = registerItem("cyanslime_spawn_egg",
            new SpawnEggItem(ModEntities.Cyanslime, 0xa86518, 0x3b260f, new FabricItemSettings()));

   private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(RUBY);
        entries.add(RAW_RUBY);
   }

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(ArcaneAbyss.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ArcaneAbyss.LOGGER.info("Registering Mod Items" + ArcaneAbyss.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(Moditems::addItemsToIngredientTabItemGroup);
    }
}
