package net.headnutandpasci.arcaneabyss.entity;

import net.headnutandpasci.arcaneabyss.entity.custom.slime.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.TestEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.GreenSlimeEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {


    public static final EntityType<TestEntity> Test = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "test"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TestEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());

    public static final EntityType<BlueSlimeEntity> BlueSlimeEntity = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "blueslime"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BlueSlimeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());
    public static final EntityType<RedSlimeEntity> RedSlimeEntity = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "redslime"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RedSlimeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());
    public static final EntityType<GreenSlimeEntity> GreenSlimeEntity = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "greenslime"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GreenSlimeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());
    public static final EntityType<DarkBlueSlimeEntity> DarkBlueSlimeEntity = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "darkblueslime"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DarkBlueSlimeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());
    public static final EntityType<DarkRedSlimeEntity> DarkRedSlimeEntity = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "darkredslime"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DarkRedSlimeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());



}
