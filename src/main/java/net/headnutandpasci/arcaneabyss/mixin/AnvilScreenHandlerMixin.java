package net.headnutandpasci.arcaneabyss.mixin;

import net.headnutandpasci.arcaneabyss.components.ModComponents;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.headnutandpasci.arcaneabyss.item.custom.SlimeStaffItem;
import net.headnutandpasci.arcaneabyss.item.custom.SlimeSwordItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(at = @At("HEAD"), method = "updateResult", cancellable = true)
    private void updateResult(CallbackInfo ci) {
        ItemStack mainStack = this.input.getStack(0);
        ItemStack secondaryStack = this.input.getStack(1);

        if (mainStack.getItem() instanceof SlimeSwordItem mainSword && secondaryStack.isOf(ModItems.SLIMESTEEL_INGOT)) {
            int level = mainSword.getUpgradeLevel(mainStack);

            if (level < SlimeSwordItem.MAX_UPGRADE_LEVEL) {
                ItemStack upgradedSword = mainStack.copy();
                ((SlimeSwordItem) upgradedSword.getItem()).upgradeSword(upgradedSword);
                upgradedSword.setDamage(0);

                this.output.setStack(0, upgradedSword);
                this.levelCost.set(5 * (level + 1));
                ci.cancel();
            }
        } else if (mainStack.getItem() instanceof SlimeStaffItem mainStaff && secondaryStack.isOf(ModItems.SLIMESTEEL_INGOT)) {
            int mainLevel = mainStaff.getUpgradeLevel(mainStack);

            if (mainLevel < SlimeStaffItem.MAX_UPGRADE_LEVEL) {
                ItemStack upgradedStaff = mainStack.copy();
                ((SlimeStaffItem) upgradedStaff.getItem()).upgradeStaff(upgradedStaff);
                upgradedStaff.setDamage(0);

                this.output.setStack(0, upgradedStaff);
                this.levelCost.set(5 * (mainLevel + 1));
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canTakeOutput", cancellable = true)
    private void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ItemStack mainStack = this.input.getStack(0);
        ItemStack secondaryStack = this.input.getStack(1);

        if ((mainStack.getItem() instanceof SlimeSwordItem || mainStack.getItem() instanceof SlimeStaffItem) && secondaryStack.isOf(ModItems.SLIMESTEEL_INGOT)) {
            int level = ModComponents.get(player).getDungeonLevel();
            cir.setReturnValue(player.getAbilities().creativeMode || level >= this.levelCost.get() && this.levelCost.get() > 0);
        }
    }

    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"))
    private void onTakeOutputExperience(PlayerEntity instance, int levels) {
        ItemStack mainStack = this.input.getStack(0);
        ItemStack secondaryStack = this.input.getStack(1);

        if ((mainStack.getItem() instanceof SlimeSwordItem || mainStack.getItem() instanceof SlimeStaffItem) && secondaryStack.isOf(ModItems.SLIMESTEEL_INGOT)) {
            ModComponents.get(instance).addDungeonLevels(levels);
        }
    }
}
