package net.headnutandpasci.arcaneabyss;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.headnutandpasci.arcaneabyss.block.ModBlockEntities;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.components.ModComponents;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.grey.GreySlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.DarkRedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeStationaryEntity;
import net.headnutandpasci.arcaneabyss.item.ModEnchantments;
import net.headnutandpasci.arcaneabyss.item.ModItemGroups;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.headnutandpasci.arcaneabyss.networking.MovementControlPacket;
import net.headnutandpasci.arcaneabyss.particle.ModParticles;
import net.headnutandpasci.arcaneabyss.recipe.ModRecipes;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.headnutandpasci.arcaneabyss.world.structures.ModStructures;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ArcaneAbyss implements ModInitializer {
    public static final String MOD_ID = "arcaneabyss";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RecipeBookCategory SLIME_STEEL_CATEGORY = RecipeBookHelper.createCategory(new Identifier(ArcaneAbyss.MOD_ID, "slime_steel_machine"));
    public static boolean disableDamage = false;

    @Override
    public void onInitialize() {
        ModParticles.init();
        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModEntities.registerModEntities();

        ModEnchantments.registerEnchantments();

        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        ModStructures.registerStructureType();

        ModRecipes.registerRecipes();

        FabricDefaultAttributeRegistry.register(ModEntities.BLUE_SLIME, BlueSlimeEntity.setAttributesBlueSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.SLIME_PILLAR, SlimePillarEntity.setAttributesSlimePillar());
        FabricDefaultAttributeRegistry.register(ModEntities.RED_SLIME, RedSlimeEntity.setAttributesRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.RED_SLIME_STATIONARY, RedSlimeStationaryEntity.setAttributesRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.GREEN_SLIME, GreenSlimeEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.DARK_BLUE_SLIME, DarkBlueSlimeEntity.setAttributesDarkBlueSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.DARK_RED_SLIME, DarkRedSlimeEntity.setAttributesDarkRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.GREY_SLIME, GreySlimeEntity.setAttributesGreySlime());
        FabricDefaultAttributeRegistry.register(ModEntities.BLACK_SLIME, BlackSlimeEntity.setAttributesBlackSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.SLIMEVIATHAN, SlimeviathanEntity.setAttributesSlimeviathan());
        FabricDefaultAttributeRegistry.register(ModEntities.YALLA, YallaEntity.setAttributesYalla());

        ServerPlayNetworking.registerGlobalReceiver(MovementControlPacket.ID, (server, player, handler, buf, responseSender) -> {
            // No-op, handled client-side
        });

        List<BlockPos> temp = List.of(
                new BlockPos(-1, -2, 7),
                new BlockPos(-1, -2, 8),
                new BlockPos(-1, -2, 9),

                new BlockPos(0, -2, 7),

                new BlockPos(1, -2, 7),
                new BlockPos(1, -2, 8)
        );

        NbtCompound nbt = new NbtCompound();
        nbt.putLongArray("target_blocks", temp.stream().mapToLong(BlockPos::asLong).toArray());
        System.out.println(nbt);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("dungeonxp")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> Text.literal("Called /dungeonxp without sub-command"), false);
                        return 1;
                    })
                    .then(literal("add")
                            .then(argument("amount", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int amount = IntegerArgumentType.getInteger(context, "amount");
                                        context.getSource().getEntityOrThrow().getComponent(ModComponents.DUNGEON_XP).addDungeonXp(amount);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("remove").then(argument("amount", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int amount = IntegerArgumentType.getInteger(context, "amount");
                                        context.getSource().getEntityOrThrow().getComponent(ModComponents.DUNGEON_XP).addDungeonXp(-amount);
                                        return 1;
                                    })
                            )
                    )
            );
        });

        // create a command to disable damage
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("disableDamage")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        disableDamage = !disableDamage;
                        context.getSource().sendFeedback(() -> Text.literal("Damage is now " + (disableDamage ? "disabled" : "enabled")), false);
                        return 1;
                    })
            );
        });
    }
}