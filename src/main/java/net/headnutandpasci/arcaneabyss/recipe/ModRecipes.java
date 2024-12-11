package net.headnutandpasci.arcaneabyss.recipe;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ArcaneAbyss.MOD_ID, SlimeSteelRecipe.Serializer.ID),
                SlimeSteelRecipe.Serializer.INSTANCE);

        Registry.register(Registries.RECIPE_TYPE, new Identifier(ArcaneAbyss.MOD_ID, SlimeSteelRecipe.Type.ID),
                SlimeSteelRecipe.Type.INSTANCE);
    }
}
