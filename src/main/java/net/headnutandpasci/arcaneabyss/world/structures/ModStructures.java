package net.headnutandpasci.arcaneabyss.world.structures;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.world.features.config.EntityTypeConfig;
import net.headnutandpasci.arcaneabyss.world.features.entities.GenericMobFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.StructureType;

public class ModStructures {
    public static final StructureType<ArcaneJigsawStructure> ARCANE_JIGSAW_STRUCTURE_TYPE = () -> ArcaneJigsawStructure.CODEC;
    public static final Feature<EntityTypeConfig> GENERIC_MOB_FEATURE = new GenericMobFeature(EntityTypeConfig.CODEC);

    public static void registerStructureType() {
        Registry.register(Registries.STRUCTURE_TYPE,
                new Identifier(ArcaneAbyss.MOD_ID, "arcane_jigsaw_structure"),
                ARCANE_JIGSAW_STRUCTURE_TYPE
        );

        Registry.register(Registries.FEATURE,
                new Identifier(ArcaneAbyss.MOD_ID, "generic_mob_feature"),
                GENERIC_MOB_FEATURE
        );
    }
}
