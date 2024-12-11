package net.headnutandpasci.arcaneabyss;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.headnutandpasci.arcaneabyss.datagen.ModModelProvider;
import net.headnutandpasci.arcaneabyss.datagen.ModRecipeProvider;

public class ArcaneAbyssDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
    }

}
