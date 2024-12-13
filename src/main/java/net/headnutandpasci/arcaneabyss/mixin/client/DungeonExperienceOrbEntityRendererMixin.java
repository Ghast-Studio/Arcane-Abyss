package net.headnutandpasci.arcaneabyss.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.headnutandpasci.arcaneabyss.components.ModComponents;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ExperienceOrbEntityRenderer.class)
public class DungeonExperienceOrbEntityRendererMixin {

    @ModifyVariable(
            method = "render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("STORE"),
            index = 17)
    private int injectedRed(int value, @Local(argsOnly = true) ExperienceOrbEntity experienceOrb, @Local(index = 16) float age) {
        /*if (!experienceOrb.getComponent(ModComponents.DUNGEON_EXPERIENCE_COMPONENT).isDungeonExperience()) {
            return value;
        }*/

        return (int) ((MathHelper.sin(age + 0.0F) + 1.0F) * 0.5F * 180.0F + 75);
    }

    @ModifyConstant(
            method = "render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            constant = @Constant(intValue = 255))
    private int injectedGreen(int value, @Local(argsOnly = true) ExperienceOrbEntity experienceOrb, @Local(index = 16) float age) {
        /*if (!experienceOrb.getComponent(ModComponents.DUNGEON_EXPERIENCE_COMPONENT).isDungeonExperience()) {
            return value;
        }*/

        return (int) ((MathHelper.sin(age + 2.0F) + 1.0F) * 0.5F * 50.0F);
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("STORE"),
            index = 19)
    private int injectedBlue(int value, @Local(argsOnly = true) ExperienceOrbEntity experienceOrb, @Local(index = 16) float age) {
        /*if (!experienceOrb.getComponent(ModComponents.DUNGEON_EXPERIENCE_COMPONENT).isDungeonExperience()) {
            return value;
        }*/

        return (int) ((MathHelper.sin(age + 2.0F) + 1.0F) * 0.5F * 180.0F + 75);
    }

}
