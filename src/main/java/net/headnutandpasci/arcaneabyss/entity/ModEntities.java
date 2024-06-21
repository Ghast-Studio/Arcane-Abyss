package net.headnutandpasci.arcaneabyss.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.TestEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.red.DarkRedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.red.MagmaBallProjectile;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.red.RedSlimeEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModEntities {

    public static EntityType<TestEntity> Test;

    public static EntityType<BlueSlimeEntity> BLUE_SLIME;
    public static EntityType<RedSlimeEntity> RED_SLIME;
    public static EntityType<GreenSlimeEntity> GREEN_SLIME;
    public static EntityType<DarkBlueSlimeEntity> DARK_BLUE_SLIME;
    public static EntityType<DarkRedSlimeEntity> DARK_RED_SLIME;

    public static EntityType<MagmaBallProjectile> MAGMA_BALL_PROJECTILE;

    public static void registerModEntities() {
        Test = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "test"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TestEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        BLUE_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "blue_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BlueSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        RED_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "red_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        GREEN_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "green_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GreenSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        DARK_BLUE_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "dark_blue_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DarkBlueSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        DARK_RED_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "dark_red_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DarkRedSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        MAGMA_BALL_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "magma_ball_projectile"),
                FabricEntityTypeBuilder.<MagmaBallProjectile>create(SpawnGroup.MISC, MagmaBallProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

        ArcaneAbyss.LOGGER.info("Registering Entities");
    }

}
