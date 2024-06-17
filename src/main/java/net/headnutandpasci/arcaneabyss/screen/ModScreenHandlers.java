package net.headnutandpasci.arcaneabyss.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<SlimeSteelMaschineScreenHandler> SLIME_STEEL_MASCHINE_SCREEN_HANDLER_SCREEN_HANDLER_TYPE =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ArcaneAbyss.MOD_ID, "slimesteel_crafting"),
                    new ExtendedScreenHandlerType<>(SlimeSteelMaschineScreenHandler::new));
    public static void registerScreenHandlers() {
        ArcaneAbyss.LOGGER.info("Registering Mod Screen Handlers" + ArcaneAbyss.MOD_ID);
    }
}
