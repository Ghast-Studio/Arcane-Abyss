package net.headnutandpasci.arcaneabyss.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.headnutandpasci.arcaneabyss.util.interfaces.RingAttackable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        TrinketsApi.getTrinketComponent(player).ifPresent(component -> component.getAllEquipped().forEach(pair -> {
            if (pair.getRight().getItem() instanceof RingAttackable) {
                ((RingAttackable) pair.getRight().getItem()).onPlayerAttacked(player, source);
            }
        }));
    }
}
