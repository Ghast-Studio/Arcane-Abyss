package net.headnutandpasci.arcaneabyss.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.DarkRedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeBallProjectile;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeStationaryEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static EntityType<BlueSlimeEntity> BLUE_SLIME;
    public static EntityType<SlimePillarEntity> SLIME_PILLAR;
    public static EntityType<RedSlimeEntity> RED_SLIME;
    public static EntityType<GreenSlimeEntity> GREEN_SLIME;
    public static EntityType<DarkBlueSlimeEntity> DARK_BLUE_SLIME;
    public static EntityType<DarkRedSlimeEntity> DARK_RED_SLIME;
    public static EntityType<RedSlimeStationaryEntity> RED_SLIME_STATIONARY;
    public static EntityType<BlackSlimeEntity> BLACK_SLIME;
    public static EntityType<SlimeviathanEntity> SLIMEVIATHAN;
    public static EntityType<SlimeBallProjectile> SLIME_BALL_PROJECTILE;
    public static EntityType<YallaEntity> YALLA;

    private static <T extends EntityType<?>> T register(T entityType, String id) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(ArcaneAbyss.MOD_ID, id), entityType);
    }

    public static void registerModEntities() {
        BLUE_SLIME = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BlueSlimeEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "blue_slime");

        SLIME_PILLAR = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SlimePillarEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "slime_pillar");

        RED_SLIME = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedSlimeEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "red_slime");

        RED_SLIME_STATIONARY = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedSlimeStationaryEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "red_slime_stationary");

        GREEN_SLIME = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GreenSlimeEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "green_slime");

        DARK_BLUE_SLIME = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DarkBlueSlimeEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "dark_blue_slime");

        DARK_RED_SLIME = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DarkRedSlimeEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "dark_red_slime");

        BLACK_SLIME = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BlackSlimeEntity::new)
                .dimensions(EntityDimensions.changing(2f, 2f)).build(), "black_slime");

        SLIMEVIATHAN = register(FabricEntityTypeBuilder.create(SpawnGroup.MISC, SlimeviathanEntity::new)
                .dimensions(EntityDimensions.changing(2f, 2f)).build(), "slimeviathan");

        SLIME_BALL_PROJECTILE = register(FabricEntityTypeBuilder.<SlimeBallProjectile>create(SpawnGroup.MISC, SlimeBallProjectile::new)
                .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build(), "magma_ball_projectile");

        YALLA = register(FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, YallaEntity::new)
                .dimensions(EntityDimensions.fixed(1f, 1f)).build(), "yalla");

        ArcaneAbyss.LOGGER.info("Registering Entities");
    }

}
