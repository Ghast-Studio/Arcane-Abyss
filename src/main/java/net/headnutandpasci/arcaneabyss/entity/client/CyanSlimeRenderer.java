package net.headnutandpasci.arcaneabyss.entity.client;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.CyanSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.PorcupineEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class CyanSlimeRenderer extends MobEntityRenderer<CyanSlimeEntity, CyanSlimeModel<CyanSlimeEntity>> {


    private static final Identifier TEXTURE = new Identifier(ArcaneAbyss.MOD_ID, "textures/entity/cyanslime.png");

    public CyanSlimeRenderer(EntityRendererFactory.Context context) {
        super(context, new CyanSlimeModel<>(context.getPart(ModModelLayers.cyanslime)), 0.6f);
    }

    @Override
    public Identifier getTexture(CyanSlimeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(CyanSlimeEntity mobEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {

        if(mobEntity.isBaby()){
            matrixStack.scale(0.5f,0.5f,0.5f);
        } else{
            matrixStack.scale(1f,1f,1f);
        }


        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
