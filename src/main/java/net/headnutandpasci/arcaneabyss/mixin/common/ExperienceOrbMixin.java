package net.headnutandpasci.arcaneabyss.mixin.common;

import net.headnutandpasci.arcaneabyss.components.ModComponents;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbMixin {
    @Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperience(I)V"))
    private void onPlayerCollision(PlayerEntity instance, int experience) {
        if (instance.world.isClient) return;

        boolean isDungeon = ModComponents.get((ExperienceOrbEntity) (Object) this).isDungeonExperience();

        if (isDungeon) {
            instance.getComponent(ModComponents.DUNGEON_XP).addDungeonXp(experience);
        } else {
            instance.addExperience(experience);
        }
    }
}
