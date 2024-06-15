// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
package net.headnutandpasci.arcaneabyss.entity.client;


import net.headnutandpasci.arcaneabyss.entity.animation.CyanSlimeAnimations;
import net.headnutandpasci.arcaneabyss.entity.animation.PorcupineAnimations;
import net.headnutandpasci.arcaneabyss.entity.custom.CyanSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.PorcupineEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class CyanSlimeModel<T extends CyanSlimeEntity> extends SinglePartEntityModel<T> {
	private final ModelPart cyanslime;
	private final ModelPart head;

	public CyanSlimeModel(ModelPart root) {
		this.cyanslime = root.getChild("cyanslime");
		this.head = cyanslime.getChild("head");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData cyanslime = modelPartData.addChild("cyanslime", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -9.0F, -5.0F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData head = cyanslime.addChild("head", ModelPartBuilder.create().uv(-2, 17).cuboid(-2.5F, -6.5F, -2.5F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(CyanSlimeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.animateMovement(CyanSlimeAnimations.WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.setHeadAngles(netHeadYaw, headPitch);
		this.updateAnimation(entity.idleAnimationState, CyanSlimeAnimations.IDLE, ageInTicks, 1f);
	}


	public void setHeadAngles(float headYaw, float headPitch){
		headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
		headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}


	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		cyanslime.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return cyanslime;
	}



}