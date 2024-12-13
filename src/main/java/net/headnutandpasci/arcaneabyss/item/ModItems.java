package net.headnutandpasci.arcaneabyss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;

import net.headnutandpasci.arcaneabyss.item.custom.*;

import net.minecraft.item.*;

import net.headnutandpasci.arcaneabyss.item.custom.SlimeStaffItem;
import net.headnutandpasci.arcaneabyss.item.custom.SlimeSwordItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.minecraft.util.Rarity.EPIC;


public class ModItems {
    public static final Item SLIMESTEEL_INGOT = registerItem("slimesteel_ingot", new Item(new FabricItemSettings()));

    public static final Item SLIME_CRYSTALLISATION = registerItem("slime_crystallisation", new Item(new FabricItemSettings()));
    public static final Item OBSIDIANSTEEL_INGOT = registerItem("obsidiansteel_ingot", new Item(new FabricItemSettings()));



    public static final Item SLIME_FLESH = registerItem("slime_flesh", new Item(new FabricItemSettings()));
    public static final Item SLIME_JUICE = registerItem("slime_juice", new Item(new FabricItemSettings()));
    public static final Item COOKED_SLIME_MEAT = registerItem("cooked_slime_meat", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_SLIME_MEAT)));

    public static final Item SLIME_STAFF = registerItem("slime_staff", new SlimeStaffItem(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item RING_OF_DEFENSE = registerItem("ring_of_defense", new RingOfDefense(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item STOMP_RING = registerItem("stomp_ring", new StompRing(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item TELEPORT_RING = registerItem("teleport_ring", new TeleportRing(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item BULWARK_STOMP_RING = registerItem("bulwark_stomp_ring", new BulwarkStompRing(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item SLIME_SWORD = registerItem("slime_sword", new SlimeSwordItem(new FabricItemSettings().rarity(EPIC)));

    public static final Item SLIME_STEEL_BALL = registerItem("slime_steel_ball", new Item(new FabricItemSettings().rarity(EPIC).maxCount(1)));

    //public static final Item RUBY_HELMET = registerItem("ruby_helmet", new ModArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    //public static final Item RUBY_CHESTPLATE = registerItem("ruby_chestplate", new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    //public static final Item RUBY_LEGGINGS = registerItem("ruby_leggings", new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    //public static final Item RUBY_BOOTS = registerItem("ruby_boots", new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(SLIMESTEEL_INGOT);
        entries.add(OBSIDIANSTEEL_INGOT);
        entries.add(SLIME_CRYSTALLISATION);
        entries.add(SLIME_FLESH);
        entries.add(SLIME_JUICE);
        entries.add(SLIME_STEEL_BALL);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ArcaneAbyss.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ArcaneAbyss.LOGGER.info("Registering Mod Items" + ArcaneAbyss.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientTabItemGroup);
    }
}
