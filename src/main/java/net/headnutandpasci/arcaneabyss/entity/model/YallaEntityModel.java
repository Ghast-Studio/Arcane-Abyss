package net.headnutandpasci.arcaneabyss.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.headnutandpasci.arcaneabyss.entity.miscEntites.YallaEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
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
    private static final float field_38999 = 0.7853982F;
    private static final float field_39000 = -1.134464F;
    private static final float field_39001 = -1.0471976F;

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

    public ModelPart getPart() {
        return this.root;
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



    public void setAngles(YallaEntity yallaEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        float k = h * 20.0F * 0.017453292F + f;
        float l = MathHelper.cos(k) * 3.1415927F * 0.15F + g;
        float m = h - (float)yallaEntity.age;
        float n = h * 9.0F * 0.017453292F;
        float o = Math.min(g / 0.3F, 1.0F);
        float p = 1.0F - o;
        float q = yallaEntity.method_43397(m);
        float r;
        float s;
        float t;
        if (yallaEntity.isDancing()) {
            r = h * 8.0F * 0.017453292F + g;
            s = MathHelper.cos(r) * 16.0F * 0.017453292F;
            t = yallaEntity.method_44368(m);
            float u = MathHelper.cos(r) * 14.0F * 0.017453292F;
            float v = MathHelper.cos(r) * 30.0F * 0.017453292F;
            this.root.yaw = yallaEntity.method_44360() ? 12.566371F * t : this.root.yaw;
            this.root.roll = s * (1.0F - t);
            this.head.yaw = v * (1.0F - t);
            this.head.roll = u * (1.0F - t);
        } else {
            this.head.pitch = j * 0.017453292F;
            this.head.yaw = i * 0.017453292F;
        }

        this.rightWing.pitch = 0.43633232F * (1.0F - o);
        this.rightWing.yaw = -0.7853982F + l;
        this.leftWing.pitch = 0.43633232F * (1.0F - o);
        this.leftWing.yaw = 0.7853982F - l;
        this.body.pitch = o * 0.7853982F;
        r = q * MathHelper.lerp(o, -1.0471976F, -1.134464F);
        ModelPart var10000 = this.root;
        var10000.pivotY += (float)Math.cos((double)n) * 0.25F * p;
        this.rightArm.pitch = r;
        this.leftArm.pitch = r;
        s = p * (1.0F - q);
        t = 0.43633232F - MathHelper.cos(n + 4.712389F) * 3.1415927F * 0.075F * s;
        this.leftArm.roll = -t;
        this.rightArm.roll = t;
        this.rightArm.yaw = 0.27925268F * q;
        this.leftArm.yaw = -0.27925268F * q;
    }

    public void setArmAngle(Arm arm, MatrixStack matrices) {
        float f = 1.0F;
        float g = 3.0F;
        this.root.rotate(matrices);
        this.body.rotate(matrices);
        matrices.translate(0.0F, 0.0625F, 0.1875F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(this.rightArm.pitch));
        matrices.scale(0.7F, 0.7F, 0.7F);
        matrices.translate(0.0625F, 0.0F, 0.0F);
    }
}