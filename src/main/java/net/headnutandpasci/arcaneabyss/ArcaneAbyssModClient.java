package net.headnutandpasci.arcaneabyss;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.*;
import net.minecraft.client.render.entity.SlimeEntityRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(){


        EntityRendererRegistry.register(ModEntities.Test, TestRenderer::new);
        EntityRendererRegistry.register(ModEntities.BlueSlimeEntity, BlueSlimeRenderer::new);


    }



}
