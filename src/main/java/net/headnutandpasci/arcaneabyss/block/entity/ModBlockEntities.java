package net.headnutandpasci.arcaneabyss.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<SlimeSteelMaschineBlockEntity> SLIMESTEELMASCHINE_BLOCK_Entity =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ArcaneAbyss.MOD_ID, "slimesteelmaschine"),
                    FabricBlockEntityTypeBuilder.create(SlimeSteelMaschineBlockEntity::new,
                            ModBlocks.SlIMESTEEL_MASCHINE).build());
    public static void registerBlockentities(){
        ArcaneAbyss.LOGGER.info("Registering block entities" + ArcaneAbyss.MOD_ID);
    }
}
