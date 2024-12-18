package net.headnutandpasci.arcaneabyss.entity.client.slime.grey;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.grey.GreySlimeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GreySlimeRenderer extends MobEntityRenderer<GreySlimeEntity, SlimeEntityModel<GreySlimeEntity>> {
    private static final Identifier TEXTURE = new Identifier(ArcaneAbyss.MOD_ID, "textures/entity/slime/greyslime.png");

    public GreySlimeRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.25F);
        this.addFeature(new SlimeOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    public void render(GreySlimeEntity slimeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = 0.25F * (float) slimeEntity.getSize();
        super.render(slimeEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(GreySlimeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(GreySlimeEntity slimeEntity, MatrixStack matrixStack, float f) {
        float g = 0.999F;
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = (float) slimeEntity.getSize();
        float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }
}