package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.headnutandpasci.arcaneabyss.block.ModBlockEntities;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.TestEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.DarkRedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeStationaryEntity;
import net.headnutandpasci.arcaneabyss.item.ModItemGroups;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.headnutandpasci.arcaneabyss.networking.MovementControlPacket;
import net.headnutandpasci.arcaneabyss.recipe.ModRecipes;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneAbyss implements ModInitializer {
    public static final String MOD_ID = "arcaneabyss";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final DefaultParticleType SlimeviathanParticle = FabricParticleTypes.simple();


    @Override
    public void onInitialize() {
        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModEntities.registerModEntities();

        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        ModRecipes.registerRecipes();

        CustomPortalBuilder.beginPortal()
                .frameBlock(ModBlocks.RUBY_BLOCK)
                .lightWithItem(ModItems.RUBY)
                .destDimID(new Identifier(ArcaneAbyss.MOD_ID, "kaupendim"))
                .tintColor(0xFFB71C1C)
                .registerPortal();
        FabricDefaultAttributeRegistry.register(ModEntities.BLUE_SLIME, BlueSlimeEntity.setAttributesBlueSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.SLIME_PILLAR, SlimePillarEntity.setAttributesSlimePillar());
        FabricDefaultAttributeRegistry.register(ModEntities.RED_SLIME, RedSlimeEntity.setAttributesRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.RED_SLIME_STATIONARY, RedSlimeStationaryEntity.setAttributesRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.GREEN_SLIME, GreenSlimeEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.DARK_BLUE_SLIME, DarkBlueSlimeEntity.setAttributesDarkBlueSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.DARK_RED_SLIME, DarkRedSlimeEntity.setAttributesDarkRedSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.BLACK_SLIME, BlackSlimeEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.SLIMEVIATHAN, SlimeviathanEntity.setAttributesGreenSlime());
        FabricDefaultAttributeRegistry.register(ModEntities.Test, TestEntity.setAttributesTest());

        ServerPlayNetworking.registerGlobalReceiver(MovementControlPacket.ID, (server, player, handler, buf, responseSender) -> {
            // No-op, handled client-side
        });

        //Registry.register(Registry., new Identifier("mymod", "my_custom_particle"), SlimeviathanParticle);
    }
}