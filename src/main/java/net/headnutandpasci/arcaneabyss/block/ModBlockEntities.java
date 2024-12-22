package net.headnutandpasci.arcaneabyss.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.entity.DungeonLeverBlockEntity;
import net.headnutandpasci.arcaneabyss.block.entity.SlimeSteelMachineBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<SlimeSteelMachineBlockEntity> SLIMESTEEL_MACHINE_ENTITY;
    public static BlockEntityType<DungeonLeverBlockEntity> DUNGEON_LEVER_ENTITY;


    public static void registerBlockEntities() {
        SLIMESTEEL_MACHINE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ArcaneAbyss.MOD_ID, "slimesteel_machine"),
                FabricBlockEntityTypeBuilder.create(SlimeSteelMachineBlockEntity::new,
                        ModBlocks.SLIMESTEEL_MACHINE).build());

        DUNGEON_LEVER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ArcaneAbyss.MOD_ID, "dungeon_lever"),
                FabricBlockEntityTypeBuilder.create(DungeonLeverBlockEntity::new,
                        ModBlocks.DUNGEON_LEVER).build());

        ArcaneAbyss.LOGGER.info("Registering block entities" + ArcaneAbyss.MOD_ID);
    }
}
