package net.headnutandpasci.arcaneabyss.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<SlimeSteelMachineScreenHandler> SLIMESTEEL_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        SLIMESTEEL_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier(ArcaneAbyss.MOD_ID, "slimesteel_crafting"),
                new ExtendedScreenHandlerType<>(SlimeSteelMachineScreenHandler::new));

        ArcaneAbyss.LOGGER.info("Registering Mod Screen Handlers" + ArcaneAbyss.MOD_ID);
    }
}
