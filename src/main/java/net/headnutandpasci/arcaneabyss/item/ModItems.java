package net.headnutandpasci.arcaneabyss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.item.custom.RubySwordItem;
import net.headnutandpasci.arcaneabyss.item.custom.RubyStaffItem;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.minecraft.util.Rarity.EPIC;


public class ModItems {
    public static final Item RUBY = registerItem("ruby", new Item(new FabricItemSettings()));
    public static final Item RAW_RUBY = registerItem("raw_ruby", new Item(new FabricItemSettings()));
    public static final Item SLIMESTEEL_INGOT = registerItem("slimesteel_ingot", new Item(new FabricItemSettings()));
    public static final Item SLIME_FLESH = registerItem("slime_flesh", new Item(new FabricItemSettings()));
    public static final Item SLIME_JUICE = registerItem("slime_juice", new Item(new FabricItemSettings()));

    //public static final Item TOMATO = registerItem("tomato", new Item(new FabricItemSettings().food(ModFoodComponents.TOMATO)));
    //public static final Item SLIME_CAKE = registerItem("slime_cake", new Item(new FabricItemSettings().food(ModFoodComponents.SLIME_CAKE)));
    public static final Item COOKED_SLIME_MEAT = registerItem("cooked_slime_meat", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_SLIME_MEAT)));
    public static final Item RUBY_STAFF = registerItem("ruby_staff", new RubyStaffItem(new FabricItemSettings().rarity(EPIC).maxCount(1)));

    public static final Item RUBY_SWORD = registerItem("ruby_sword", new RubySwordItem(new FabricItemSettings().rarity(EPIC)));

    //public static final Item RUBY_HELMET = registerItem("ruby_helmet", new ModArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    //public static final Item RUBY_CHESTPLATE = registerItem("ruby_chestplate", new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    //public static final Item RUBY_LEGGINGS = registerItem("ruby_leggings", new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    //public static final Item RUBY_BOOTS = registerItem("ruby_boots", new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(RUBY);
        entries.add(RAW_RUBY);
        entries.add(SLIMESTEEL_INGOT);
        entries.add(SLIME_FLESH);
        entries.add(SLIME_JUICE);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ArcaneAbyss.MOD_ID, name), item);

    }

    public static void registerModItems() {
        ArcaneAbyss.LOGGER.info("Registering Mod Items" + ArcaneAbyss.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientTabItemGroup);
    }
}
