package net.headnutandpasci.arcaneabyss.mixin.client;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useSlimeStaffModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.SLIME_STAFF) && renderMode != ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).arcaneabyss$getModels().getModelManager().getModel(new ModelIdentifier(ArcaneAbyss.MOD_ID, "slime_staff_3d", "inventory"));
        }
        return value;
    }
}
