package net.headnutandpasci.arcaneabyss;

import net.fabricmc.api.ModInitializer;

import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.ModItemGroups;
import net.headnutandpasci.arcaneabyss.item.Moditems;
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
	}
}