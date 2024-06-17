package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.BlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.GreenSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.RedSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.TestRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {


        EntityRendererRegistry.register(ModEntities.Test, TestRenderer::new);
        EntityRendererRegistry.register(ModEntities.BlueSlimeEntity, BlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.RedSlimeEntity, RedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.GreenlimeEntity, GreenSlimeRenderer::new);


    }


}
