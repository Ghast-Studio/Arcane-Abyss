package net.headnutandpasci.arcaneabyss.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent TOMATO = new FoodComponent.Builder().hunger(3).saturationModifier(0.25f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,100,2),0.25f).build();
}
