package net.headnutandpasci.arcaneabyss.mixin;

import net.headnutandpasci.arcaneabyss.item.custom.SlimeStaffItem;
import net.headnutandpasci.arcaneabyss.item.custom.SlimeSwordItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(at = @At("HEAD"), method = "updateResult", cancellable = true)
    private void init(CallbackInfo ci) {
        ItemStack mainStack = this.input.getStack(0);
        ItemStack secondaryStack = this.input.getStack(1);

        if (mainStack.getItem() instanceof SlimeSwordItem mainSword && secondaryStack.getItem() instanceof SlimeSwordItem secondarySword) {
            int mainLevel = mainSword.getUpgradeLevel(mainStack);
            int secondaryLevel = secondarySword.getUpgradeLevel(secondaryStack);

            if (mainLevel < SlimeSwordItem.MAX_UPGRADE_LEVEL && mainLevel == secondaryLevel) {
                ItemStack upgradedSword = mainStack.copy();
                ((SlimeSwordItem) upgradedSword.getItem()).upgradeSword(upgradedSword);
                upgradedSword.setDamage(0);

                this.output.setStack(0, upgradedSword);
                this.levelCost.set(5 * (mainLevel + 1));
                ci.cancel();
            }
        } else if (mainStack.getItem() instanceof SlimeStaffItem mainStaff && secondaryStack.getItem() instanceof SlimeStaffItem secondaryStaff) {
            int mainLevel = mainStaff.getUpgradeLevel(mainStack);
            int secondaryLevel = secondaryStaff.getUpgradeLevel(secondaryStack);

            if (mainLevel < SlimeStaffItem.MAX_UPGRADE_LEVEL && mainLevel == secondaryLevel) {
                ItemStack upgradedSword = mainStack.copy();
                ((SlimeStaffItem) upgradedSword.getItem()).upgradeStaff(upgradedSword);
                upgradedSword.setDamage(0);

                this.output.setStack(0, upgradedSword);
                this.levelCost.set(5 * (mainLevel + 1));
                ci.cancel();
            }
        }
    }
}
