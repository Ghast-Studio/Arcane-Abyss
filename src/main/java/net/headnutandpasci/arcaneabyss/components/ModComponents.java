package net.headnutandpasci.arcaneabyss.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<PlayerDungeonExperienceComponent> DUNGEON_XP = ComponentRegistry.getOrCreate(
            new Identifier(ArcaneAbyss.MOD_ID, "dungeon_xp"),
            PlayerDungeonExperienceComponent.class
    );

    public static final ComponentKey<DungeonExperienceComponent> DUNGEON_EXPERIENCE_COMPONENT = ComponentRegistry.getOrCreate(
            new Identifier(ArcaneAbyss.MOD_ID, "dungeon_experience"),
            DungeonExperienceComponent.class
    );

    public static PlayerDungeonExperienceComponent get(PlayerEntity player) {
        return player.getComponent(DUNGEON_XP);
    }

    public static DungeonExperienceComponent get(ExperienceOrbEntity entity) {
        return entity.getComponent(DUNGEON_EXPERIENCE_COMPONENT);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(DUNGEON_XP, PlayerDungeonExperienceComponent::new, RespawnCopyStrategy.INVENTORY);
        registry.registerFor(ExperienceOrbEntity.class, DUNGEON_EXPERIENCE_COMPONENT, DungeonExperienceComponent::new);
    }
}
