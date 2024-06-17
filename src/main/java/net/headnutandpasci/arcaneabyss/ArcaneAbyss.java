package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ModInitializer;

import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.block.entity.ModBlockEntities;
import net.headnutandpasci.arcaneabyss.item.ModItemGroups;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneAbyss implements ModInitializer {
	public static final String MOD_ID = "arcaneabyss";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItemGroups.registerItemGroups();

		Moditems.registerModItems();
		ModBlocks.registerModBlocks();

		ModBlockEntities.registerBlockentities();
		ModScreenHandlers.registerScreenHandlers();
		CustomPortalBuilder.beginPortal()
				.frameBlock(ModBlocks.RUBY_BLOCK)
				.lightWithItem(Moditems.RUBY)
				.destDimID(new Identifier(ArcaneAbyss.MOD_ID, "kaupendim"))
				.tintColor(0xFFB71C1C)
				.registerPortal();
	}
}