package net.headnutandpasci.arcaneabyss;

import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.headnutandpasci.arcaneabyss.block.ModBlockEntities;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.DarkRedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeStationaryEntity;
import net.headnutandpasci.arcaneabyss.item.ModEnchantments;
import net.headnutandpasci.arcaneabyss.item.ModItemGroups;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.headnutandpasci.arcaneabyss.item.enchantments.StickyDefense;
import net.headnutandpasci.arcaneabyss.networking.MovementControlPacket;
import net.headnutandpasci.arcaneabyss.recipe.ModRecipes;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.headnutandpasci.arcaneabyss.world.structures.ModStructures;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneAbyss implements ModInitializer {
    public static final String MOD_ID = "arcaneabyss";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static RecipeBookCategory SLIME_STEEL_CATEGORY = RecipeBookHelper.createCategory(new Identifier(ArcaneAbyss.MOD_ID, "slime_steel_machine"));

    @Override
    public void onInitialize() {
        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModEntities.registerModEntities();

        ModEnchantments.registerEnchantments();

        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        ModStructures.registerStructureType();

        ModRecipes.registerRecipes();

        // Register tick event for checking StickyDefense
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            world.getPlayers().forEach(StickyDefense::tick);
        });

        FabricDefaultAttributeRegistry.register(ModEntities.BLUE_SLIME, BlueSlimeEntity.setAttributesBlueSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.SLIME_PILLAR, SlimePillarEntity.setAttributesSlimePillar());
        FabricDefaultAttributeRegistry.register(ModEntities.RED_SLIME, RedSlimeEntity.setAttributesRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.RED_SLIME_STATIONARY, RedSlimeStationaryEntity.setAttributesRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.GREEN_SLIME, GreenSlimeEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.DARK_BLUE_SLIME, DarkBlueSlimeEntity.setAttributesDarkBlueSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.DARK_RED_SLIME, DarkRedSlimeEntity.setAttributesDarkRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.BLACK_SLIME, BlackSlimeEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.SLIMEVIATHAN, SlimeviathanEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.YALLA, YallaEntity.setAttributesYalla());

        ServerPlayNetworking.registerGlobalReceiver(MovementControlPacket.ID, (server, player, handler, buf, responseSender) -> {
            // No-op, handled client-side
        });
    }
}