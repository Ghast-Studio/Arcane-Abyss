package net.headnutandpasci.arcaneabyss.world.structures;

import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.structure.StructureType;

public class ModStructures {
    public static final RegistryEntry<StructureType<?>> STRUCTURE_TYPE = RegistryEntry.of(Registries.STRUCTURE_TYPE, "arcaneabyss:structure_type");

    public static final RegistryEntry<StructureType<ArcaneJigsawStructure>> GENERIC_JIGSAW_STRUCTURE = STRUCTURE_TYPE.register("generic_jigsaw_structure", () -> () -> GenericJigsawStructure.CODEC);

    public static void init() {}
}
