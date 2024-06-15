package net.headnutandpasci.arcaneabyss;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.ModModelLayers;
import net.headnutandpasci.arcaneabyss.entity.client.PorcupineModel;
import net.headnutandpasci.arcaneabyss.entity.client.PorcupineRenderer;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(){
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PORCUPINE, PorcupineModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.PORCUPINE, PorcupineRenderer::new);
    }

}
