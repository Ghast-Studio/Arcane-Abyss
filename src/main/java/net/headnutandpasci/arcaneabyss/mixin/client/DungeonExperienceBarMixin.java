package net.headnutandpasci.arcaneabyss.mixin.client;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.components.ModComponents;
import net.headnutandpasci.arcaneabyss.components.PlayerDungeonExperienceComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class DungeonExperienceBarMixin {
    @Shadow
    @Final
    private static Identifier ICONS;
    @Unique
    private final Identifier DUNGEON_XP_BAR = new Identifier(ArcaneAbyss.MOD_ID, "textures/gui/dungeon_xp_bar.png");
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Shadow
    public abstract void renderExperienceBar(DrawContext context, int x);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"))
    private void injected(InGameHud instance, DrawContext context, int x) {
        if (this.client.player == null)
            return;

        PlayerDungeonExperienceComponent component = ModComponents.get(this.client.player);

        renderExperienceBar(context, x);

        this.client.getProfiler().push("dunExpBar");
        int nextLevelExperience = component.getNextLevelExperience();
        int experienceBarWidth;
        int experienceBarY;
        int height = 42;

        if (nextLevelExperience > 0) {
            experienceBarWidth = (int) (component.getDungeonProgress() * 183.0F);
            experienceBarY = (this.scaledHeight - height) + 3;
            context.drawTexture(ICONS, x, experienceBarY, 0, 64, 182, 5);
            if (experienceBarWidth > 0) {
                context.drawTexture(DUNGEON_XP_BAR, x, experienceBarY, 0, 69, experienceBarWidth, 5);
            }

            this.client.getProfiler().pop();
            if (component.getDungeonLevel() > 0) {
                this.client.getProfiler().push("dunExpLevel");
                String string = String.valueOf(component.getDungeonLevel());
                experienceBarWidth = (this.scaledWidth - this.getTextRenderer().getWidth(string)) / 2;
                experienceBarY = this.scaledHeight - height;
                context.drawText(this.getTextRenderer(), string, experienceBarWidth + 1, experienceBarY, 0, false);
                context.drawText(this.getTextRenderer(), string, experienceBarWidth - 1, experienceBarY, 0, false);
                context.drawText(this.getTextRenderer(), string, experienceBarWidth, experienceBarY + 1, 0, false);
                context.drawText(this.getTextRenderer(), string, experienceBarWidth, experienceBarY - 1, 0, false);
                context.drawText(this.getTextRenderer(), string, experienceBarWidth, experienceBarY, 16733695, false);
                this.client.getProfiler().pop();
            }
        }
    }

    @ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 4, ordinal = 0))
    private int injectedXpBar(int value) {
        return value - 3;
    }

    @ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 39, ordinal = 0))
    private int injectedStatusBar(int value) {
        return value + 11;
    }
}
