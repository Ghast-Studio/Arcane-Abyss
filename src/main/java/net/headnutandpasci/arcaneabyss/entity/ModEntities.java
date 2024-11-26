package net.headnutandpasci.arcaneabyss.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.client.TestEntity;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeviathanProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.DarkRedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.MagmaBallProjectile;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeStationaryEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static EntityType<TestEntity> Test;

    public static EntityType<BlueSlimeEntity> BLUE_SLIME;
    public static EntityType<SlimePillarEntity> SLIME_PILLAR;
    public static EntityType<RedSlimeEntity> RED_SLIME;
    public static EntityType<GreenSlimeEntity> GREEN_SLIME;
    public static EntityType<DarkBlueSlimeEntity> DARK_BLUE_SLIME;
    public static EntityType<DarkRedSlimeEntity> DARK_RED_SLIME;
    public static EntityType<RedSlimeStationaryEntity> RED_SLIME_STATIONARY;
    public static EntityType<BlackSlimeEntity> BLACK_SLIME;
    public static EntityType<SlimeviathanEntity> SLIMEVIATHAN;
    public static EntityType<MagmaBallProjectile> MAGMA_BALL_PROJECTILE;
    public static EntityType<BlackSlimeProjectileEntity> BLACK_SLIME_PROJECTILE;
    public static EntityType<SlimeviathanProjectileEntity> SLIMEVIATHAN_PROJECTLE;
    public static EntityType<YallaEntity> YALLA;

    public static void registerModEntities() {
        Test = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "test"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TestEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        BLUE_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "blue_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BlueSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());
        SLIME_PILLAR = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "slime_pillar"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SlimePillarEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        RED_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "red_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());
        RED_SLIME_STATIONARY = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "red_slime_stationary"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedSlimeStationaryEntity::new)
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

        BLACK_SLIME = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "black_slime"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BlackSlimeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());

        MAGMA_BALL_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "magma_ball_projectile"),
                FabricEntityTypeBuilder.<MagmaBallProjectile>create(SpawnGroup.MISC, MagmaBallProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
        BLACK_SLIME_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "black_slime_projectile"),
                FabricEntityTypeBuilder.<BlackSlimeProjectileEntity>create(SpawnGroup.MISC, BlackSlimeProjectileEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
        SLIMEVIATHAN_PROJECTLE = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "slimeviathan_projectile"),
                FabricEntityTypeBuilder.<SlimeviathanProjectileEntity>create(SpawnGroup.MISC, SlimeviathanProjectileEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
        SLIMEVIATHAN = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "slimeviathan"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, SlimeviathanEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());
        YALLA = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "yalla"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, YallaEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f)).build());


        ArcaneAbyss.LOGGER.info("Registering Entities");
    }

}
