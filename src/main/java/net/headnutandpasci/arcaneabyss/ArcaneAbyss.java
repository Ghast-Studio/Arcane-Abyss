package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.*;
import net.headnutandpasci.arcaneabyss.item.ModItemGroups;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.headnutandpasci.arcaneabyss.entity.custom.TestEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.GreenSlimeEntity;

public class ArcaneAbyss implements ModInitializer {
	public static final String MOD_ID = "arcaneabyss";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItemGroups.registerItemGroups();

		Moditems.registerModItems();
		ModBlocks.registerModBlocks();

		CustomPortalBuilder.beginPortal()
				.frameBlock(ModBlocks.RUBY_BLOCK)
				.lightWithItem(Moditems.RUBY)
				.destDimID(new Identifier(ArcaneAbyss.MOD_ID, "kaupendim"))
				.tintColor(0xFFB71C1C)
				.registerPortal();


		FabricDefaultAttributeRegistry.register(ModEntities.Test, TestEntity.setAttributesTest());
		FabricDefaultAttributeRegistry.register(ModEntities.BlueSlimeEntity, BlueSlimeEntity.setAttributesBlueSlime());
		FabricDefaultAttributeRegistry.register(ModEntities.RedSlimeEntity, RedSlimeEntity.setAttributesRedSlime());
		FabricDefaultAttributeRegistry.register(ModEntities.GreenSlimeEntity, GreenSlimeEntity.setAttributesGreenSlime());
		FabricDefaultAttributeRegistry.register(ModEntities.DarkBlueSlimeEntity, DarkBlueSlimeEntity.setAttributesDarkBlueSlime());
		FabricDefaultAttributeRegistry.register(ModEntities.DarkRedSlimeEntity, DarkRedSlimeEntity.setAttributesDarkRedSlime());
	}
}