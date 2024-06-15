package net.headnutandpasci.arcaneabyss.entity.client;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer PORCUPINE = new EntityModelLayer(
            new Identifier(ArcaneAbyss.MOD_ID, "porcupine"), "main");
}
