package net.headnutandpasci.arcaneabyss.mixin.groups;

import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeResultCollection.class)
public abstract class RecipeBookGroupMixin {

    @Inject(at = @At("TAIL"), method = "isInitialized", cancellable = true)
    public void getGroups(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
