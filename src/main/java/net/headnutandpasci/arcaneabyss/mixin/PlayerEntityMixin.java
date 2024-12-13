package net.headnutandpasci.arcaneabyss.mixin;

import net.headnutandpasci.arcaneabyss.item.custom.BulwarkStompRing;
import net.headnutandpasci.arcaneabyss.item.custom.RingOfDefense;
import net.headnutandpasci.arcaneabyss.item.custom.StompRing;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof RingOfDefense ring) {
                ring.onPlayerAttacked(player, source);
            }
            if (stack.getItem() instanceof StompRing ring) {
                ring.onPlayerDamaged(player);
            }
            if (stack.getItem() instanceof BulwarkStompRing ring) {
                ring.onPlayerAttacked(player, source);
            }
        }
    }
}
