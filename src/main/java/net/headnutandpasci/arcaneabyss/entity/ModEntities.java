package net.headnutandpasci.arcaneabyss.entity;


import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.TestEntity;
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



}
