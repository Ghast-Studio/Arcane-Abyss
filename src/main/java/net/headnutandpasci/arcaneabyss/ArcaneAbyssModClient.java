package net.headnutandpasci.arcaneabyss;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.RedSlimeStationaryRenderer;
import net.headnutandpasci.arcaneabyss.networking.MovementControlPacket;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.headnutandpasci.arcaneabyss.screen.SlimeSteelMachineScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.headnutandpasci.arcaneabyss.entity.client.*;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.BlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.DarkBlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.SlimePillarRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.boss.black.BlackSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.boss.slimeviathan.SlimeviathanRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.green.GreenSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.DarkRedSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.RedSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileRenderer;
import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeviathanProjectileRenderer;
import net.headnutandpasci.arcaneabyss.entity.slime.red.MagmaBallProjectileRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(){
        EntityRendererRegistry.register(ModEntities.Test, TestRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLUE_SLIME, BlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIME_PILLAR, SlimePillarRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_SLIME, RedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_SLIME_STATIONARY, RedSlimeStationaryRenderer::new);
        EntityRendererRegistry.register(ModEntities.GREEN_SLIME, GreenSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_BLUE_SLIME, DarkBlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_RED_SLIME, DarkRedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLACK_SLIME, BlackSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIMEVIATHAN, SlimeviathanRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGMA_BALL_PROJECTILE, MagmaBallProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLACK_SLIME_PROJECTILE, BlackSlimeProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIMEVIATHAN_PROJECTLE, SlimeviathanProjectileRenderer::new);
        HandledScreens.register(ModScreenHandlers.SLIMESTEEL_SCREEN_HANDLER, SlimeSteelMachineScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(MovementControlPacket.ID, MovementControlPacket::handle);
    }

}
