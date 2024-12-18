package net.headnutandpasci.arcaneabyss.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.item.custom.SlimeStaffItem;
import net.headnutandpasci.arcaneabyss.item.custom.SlimeSwordItem;
import net.headnutandpasci.arcaneabyss.item.custom.belt.TeleportBelt;
import net.headnutandpasci.arcaneabyss.item.custom.ring.BulwarkStompRing;
import net.headnutandpasci.arcaneabyss.item.custom.ring.DefenseRing;
import net.headnutandpasci.arcaneabyss.item.custom.ring.StompRing;
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
    public static final Item SLIME_STEEL_BALL = registerItem("slime_steel_ball", new Item(new FabricItemSettings().rarity(EPIC).maxCount(1)));

    public static final Item SLIME_FLESH = registerItem("slime_flesh", new Item(new FabricItemSettings()));
    public static final Item SLIME_JUICE = registerItem("slime_juice", new Item(new FabricItemSettings()));
    public static final Item COOKED_SLIME_MEAT = registerItem("cooked_slime_meat", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_SLIME_MEAT)));

    public static final Item DEFENSE_RING = registerItem("ring_of_defense", new DefenseRing(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item STOMP_RING = registerItem("stomp_ring", new StompRing(new FabricItemSettings().rarity(EPIC).maxCount(1)));
    public static final Item BULWARK_STOMP_RING = registerItem("bulwark_stomp_ring", new BulwarkStompRing(new FabricItemSettings().rarity(EPIC).maxCount(1)));

    public static final Item TELEPORT_BELT = registerItem("teleport_belt", new TeleportBelt(new FabricItemSettings().rarity(EPIC).maxCount(1)));

    public static final Item SLIME_SWORD = registerItem("slime_sword", new SlimeSwordItem(new FabricItemSettings().rarity(EPIC)));
    public static final Item SLIME_STAFF = registerItem("slime_staff", new SlimeStaffItem(new FabricItemSettings().rarity(EPIC).maxCount(1)));

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
