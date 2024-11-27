package net.headnutandpasci.arcaneabyss.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.custom.BlueSlimeSpawner;
import net.headnutandpasci.arcaneabyss.block.entity.BlueSlimeSpawnerEntity;
import net.headnutandpasci.arcaneabyss.block.entity.SlimeSteelMachineBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<SlimeSteelMachineBlockEntity> SLIMESTEEL_MACHINE_ENTITY;
    public static BlockEntityType<BlueSlimeSpawnerEntity> BLUE_SLIME_SPAWNER_ENTITY;

    public static void registerBlockEntities() {
        SLIMESTEEL_MACHINE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ArcaneAbyss.MOD_ID, "slimesteel_machine"),
                FabricBlockEntityTypeBuilder.create(SlimeSteelMachineBlockEntity::new,
                        ModBlocks.SLIMESTEEL_MACHINE).build());
        BLUE_SLIME_SPAWNER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ArcaneAbyss.MOD_ID, "blue_slime_spawner"),
                FabricBlockEntityTypeBuilder.create(BlueSlimeSpawnerEntity::new,
                        ModBlocks.BLUE_SLIME_SPAWNER).build());


        ArcaneAbyss.LOGGER.info("Registering block entities" + ArcaneAbyss.MOD_ID);
    }
}
