package net.headnutandpasci.arcaneabyss.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class ExampleMixin {
    /*@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
    private void init(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }*/
}