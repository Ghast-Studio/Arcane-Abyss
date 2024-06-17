package net.headnutandpasci.arcaneabyss.entity.client;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.GreenSlimeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GreenSlimeRenderer extends MobEntityRenderer<GreenSlimeEntity, SlimeEntityModel<GreenSlimeEntity>> {
    private static final Identifier TEXTURE = new Identifier(ArcaneAbyss.MOD_ID,"textures/entity/slime/greenslime.png");

    public GreenSlimeRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.25F);
        this.addFeature(new SlimeOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    public void render(GreenSlimeEntity slimeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = 0.5f;
        super.render(slimeEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(GreenSlimeEntity entity) {
        return TEXTURE;
    }

    protected void scale(GreenSlimeEntity slimeEntity, MatrixStack matrixStack, float f) {
        float g = 0.999F + slimeEntity.getClientFuseTime(f);
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = 2.0f;
        float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }




}
