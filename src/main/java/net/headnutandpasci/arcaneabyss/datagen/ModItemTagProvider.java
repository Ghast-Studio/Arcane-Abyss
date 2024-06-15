package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture){
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg){
        //ItemTags
            getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                    .add(Moditems.RUBY_HELMET, Moditems.RUBY_CHESTPLATE, Moditems.RUBY_LEGGING, Moditems.RUBY_BOOTS);
    }
}
