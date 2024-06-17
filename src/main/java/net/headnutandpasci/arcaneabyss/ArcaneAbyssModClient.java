package net.headnutandpasci.arcaneabyss;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.*;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.*;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import  net. headnutandpasci. arcaneabyss. entity. ModEntities;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.client.DarkBlueSlimeRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(){


        EntityRendererRegistry.register(ModEntities.Test, TestRenderer::new);
        EntityRendererRegistry.register(ModEntities.BlueSlimeEntity, BlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.RedSlimeEntity, RedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.GreenSlimeEntity, GreenSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DarkBlueSlimeEntity, DarkBlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DarkRedSlimeEntity, DarkRedSlimeRenderer::new);

    }



}
