package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.*;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.Test, TestRenderer::new);
        EntityRendererRegistry.register(ModEntities.BlueSlimeEntity, BlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.RedSlimeEntity, RedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.GreenSlimeEntity, GreenSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DarkBlueSlimeEntity, DarkBlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DarkRedSlimeEntity, DarkRedSlimeRenderer::new);

        EntityRendererRegistry.register(ModEntities.MAGMA_BALL_ENTITY_ENTITY_TYPE, FlyingItemEntityRenderer::new);
    }


}
