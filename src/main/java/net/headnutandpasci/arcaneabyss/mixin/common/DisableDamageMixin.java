package net.headnutandpasci.arcaneabyss.mixin.common;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class DisableDamageMixin {
    @Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
    private void init(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (ArcaneAbyss.disableDamage)
            cir.setReturnValue(true);
    }
}