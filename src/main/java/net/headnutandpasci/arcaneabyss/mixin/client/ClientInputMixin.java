package net.headnutandpasci.arcaneabyss.mixin.client;

import net.headnutandpasci.arcaneabyss.util.MovementControlAccess;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class ClientInputMixin implements MovementControlAccess {

    @Shadow
    @Final
    private GameOptions settings;
    @Unique
    private boolean movementDisabled = false;

    @Inject(method = "tick", at = @At("HEAD"))
    public void disablePlayerInput(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (movementDisabled) {
            this.settings.leftKey.setPressed(false);
            this.settings.rightKey.setPressed(false);
            this.settings.forwardKey.setPressed(false);
            this.settings.backKey.setPressed(false);
            this.settings.jumpKey.setPressed(false);
            this.settings.sneakKey.setPressed(false);
            this.settings.sprintKey.setPressed(false);
            this.settings.attackKey.setPressed(false);
        }
    }

    @Override
    public void arcane_Abyss$setMovementDisabled(boolean disabled) {
        this.movementDisabled = disabled;
    }
}
