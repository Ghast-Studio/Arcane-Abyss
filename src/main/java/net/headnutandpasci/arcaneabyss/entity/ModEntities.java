package net.headnutandpasci.arcaneabyss.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.custom.CyanSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.PorcupineEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<PorcupineEntity> PORCUPINE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "porcupine"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PorcupineEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());


    public static final EntityType<CyanSlimeEntity> Cyanslime = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ArcaneAbyss.MOD_ID, "cyanslime"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CyanSlimeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f,1f)).build());



}
