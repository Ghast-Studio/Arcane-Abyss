package net.headnutandpasci.arcaneabyss;
import net.fabricmc.api.ClientModInitializer;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.headnutandpasci.arcaneabyss.screen.SlimeSteelMaschineScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(){
        HandledScreens.register(ModScreenHandlers.SLIME_STEEL_MASCHINE_SCREEN_HANDLER_SCREEN_HANDLER_TYPE, SlimeSteelMaschineScreen::new);
    }

}
