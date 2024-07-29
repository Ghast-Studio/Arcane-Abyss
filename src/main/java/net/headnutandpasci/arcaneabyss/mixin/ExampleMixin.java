package net.headnutandpasci.arcaneabyss.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	private void init(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}