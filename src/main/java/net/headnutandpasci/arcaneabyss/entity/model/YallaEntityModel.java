package net.headnutandpasci.arcaneabyss.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class YallaEntityModel extends SinglePartEntityModel<YallaEntity> implements ModelWithArms {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public YallaEntityModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucent);
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.5F, 0.0F));
        modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.99F, 0.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 10).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F)).uv(0, 16).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, -4.0F, 0.0F));
        modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(23, 0).cuboid(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)), ModelTransform.pivot(-1.75F, 0.5F, 0.0F));
        modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(23, 6).cuboid(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)), ModelTransform.pivot(1.75F, 0.5F, 0.0F));
        modelPartData3.addChild("right_wing", ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 0.0F, 0.6F));
        modelPartData3.addChild("left_wing", ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 0.0F, 0.6F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(YallaEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Reset transformations of all model parts
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        // Calculate base values
        float timeFactor = animationProgress * 20.0F * 0.017453292F + limbAngle; // Converts time to radians with an offset
        float swingOffset = MathHelper.cos(timeFactor) * 3.1415927F * 0.15F + limbDistance;
        float phase = animationProgress - (float) entity.age;
        float cycleAngle = animationProgress * 9.0F * 0.017453292F;

        // Calculate interpolation factor for wings
        float wingFactor = Math.min(limbDistance / 0.3F, 1.0F);
        float inverseWingFactor = 1.0F - wingFactor;

        // Handle dancing animation
        if (entity.isDancing()) {
            float danceCycle = animationProgress * 8.0F * 0.017453292F + limbDistance;
            float danceRoll = MathHelper.cos(danceCycle) * 16.0F * 0.017453292F;
            float danceYaw = MathHelper.cos(danceCycle) * 14.0F * 0.017453292F;
            float dancePitch = MathHelper.cos(danceCycle) * 30.0F * 0.017453292F;
            float danceProgress = entity.getAnimationProgress(phase);

            this.root.yaw = entity.animationCycleProgress() ? 12.566371F * danceProgress : this.root.yaw;
            this.root.roll = danceRoll * (1.0F - danceProgress);
            this.head.yaw = dancePitch * (1.0F - danceProgress);
            this.head.roll = danceYaw * (1.0F - danceProgress);
        } else {
            // Handle default head movement
            this.head.pitch = headPitch * 0.017453292F;
            this.head.yaw = headYaw * 0.017453292F;
        }

        // Wing movement
        this.rightWing.pitch = 0.43633232F * (1.0F - wingFactor);
        this.rightWing.yaw = -0.7853982F + swingOffset;
        this.leftWing.pitch = 0.43633232F * (1.0F - wingFactor);
        this.leftWing.yaw = 0.7853982F - swingOffset;

        // Body movement
        this.body.pitch = wingFactor * 0.7853982F;

        // Arm and body positioning
        float armPitch = inverseWingFactor * MathHelper.lerp(wingFactor, -1.0471976F, -1.134464F);
        this.root.pivotY += (float) Math.cos(cycleAngle) * 0.25F * inverseWingFactor;
        this.rightArm.pitch = armPitch;
        this.leftArm.pitch = armPitch;

        // Arm rolling and yaw adjustments
        float rollSwing = inverseWingFactor * (1.0F - wingFactor);
        float armRollOffset = 0.43633232F - MathHelper.cos(cycleAngle + 4.712389F) * 3.1415927F * 0.075F * rollSwing;
        this.leftArm.roll = -armRollOffset;
        this.rightArm.roll = armRollOffset;
        this.rightArm.yaw = 0.27925268F * wingFactor;
        this.leftArm.yaw = -0.27925268F * wingFactor;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.root.rotate(matrices);
        this.body.rotate(matrices);
        matrices.translate(0.0F, 0.0625F, 0.1875F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(this.rightArm.pitch));
        matrices.scale(0.7F, 0.7F, 0.7F);
        matrices.translate(0.0625F, 0.0F, 0.0F);
    }
}