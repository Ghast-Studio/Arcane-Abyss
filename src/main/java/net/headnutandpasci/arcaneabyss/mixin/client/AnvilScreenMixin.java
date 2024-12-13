package net.headnutandpasci.arcaneabyss.mixin.client;

import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {
    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @ModifyConstant(
            method = "drawForeground",
            constant = @Constant(intValue = 8453920))
    private int injected(int constant) {
        if ((this.handler.getSlot(0).getStack().isOf(ModItems.SLIME_SWORD) || this.handler.getSlot(0).getStack().isOf(ModItems.SLIME_STAFF)) &&
                this.handler.getSlot(1).getStack().isOf(ModItems.SLIMESTEEL_INGOT)) {
            return 16733695;
        }

        return constant;
    }
}

