package net.headnutandpasci.arcaneabyss.entity.client;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.TestEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TestRenderer extends GeoEntityRenderer<TestEntity> {
    public TestRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TestModel());
    }

    @Override
    public Identifier getTextureLocation(TestEntity animatable) {
        return new Identifier(ArcaneAbyss.MOD_ID, "textures/entity/test.png");
    }

    @Override
    public void render(TestEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
