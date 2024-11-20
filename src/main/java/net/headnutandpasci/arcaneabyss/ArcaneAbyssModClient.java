package net.headnutandpasci.arcaneabyss;
import net.fabricmc.api.ClientModInitializer;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.headnutandpasci.arcaneabyss.screen.SlimeSteelMachineScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ArcaneAbyssModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(){
        HandledScreens.register(ModScreenHandlers.SLIMESTEEL_SCREEN_HANDLER, SlimeSteelMachineScreen::new);
    }

}
