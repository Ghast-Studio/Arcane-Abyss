package net.headnutandpasci.arcaneabyss.entity.client;


import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.TestEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

@Deprecated
public class TestModel extends GeoModel<TestEntity> {
    @Override
    public Identifier getModelResource(TestEntity animatable) {
        return new Identifier(ArcaneAbyss.MOD_ID, "geo/test.geo.json");
    }

    @Override
    public Identifier getTextureResource(TestEntity animatable) {
        return new Identifier(ArcaneAbyss.MOD_ID, "textures/entity/test.png");
    }

    @Override
    public Identifier getAnimationResource(TestEntity animatable) {
        return new Identifier(ArcaneAbyss.MOD_ID, "animations/test.animation.json");
    }

    @Override
    public void setCustomAnimations(TestEntity animatable, long instanceId, AnimationState<TestEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}