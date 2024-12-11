package net.headnutandpasci.arcaneabyss.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<DungeonXpComponent> DUNGEON_XP = ComponentRegistry.getOrCreate(
            new Identifier(ArcaneAbyss.MOD_ID, "dungeon_xp"),
            DungeonXpComponent.class
    );

    private ModComponents() {
    }

    public static DungeonXpComponent get(PlayerEntity player) {
        return DUNGEON_XP.get(player);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

    }
}
