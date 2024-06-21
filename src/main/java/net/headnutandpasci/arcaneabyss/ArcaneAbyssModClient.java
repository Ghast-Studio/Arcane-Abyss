package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.*;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.BlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.DarkBlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.green.GreenSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.DarkRedSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.RedSlimeRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.Test, TestRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLUE_SLIME, BlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_SLIME, RedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.GREEN_SLIME, GreenSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_BLUE_SLIME, DarkBlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_RED_SLIME, DarkRedSlimeRenderer::new);

        EntityRendererRegistry.register(ModEntities.MAGMA_BALL_PROJECTILE, FlyingItemEntityRenderer::new);
    }


}
