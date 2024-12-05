package net.headnutandpasci.arcaneabyss.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent TOMATO = new FoodComponent.Builder().hunger(3).saturationModifier(0.25f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,100,2),0.25f).build();
    public static final FoodComponent COOKED_SLIME_MEAT = new FoodComponent.Builder().hunger(7).saturationModifier(0.4f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,100,2),0.5f).build();
}
