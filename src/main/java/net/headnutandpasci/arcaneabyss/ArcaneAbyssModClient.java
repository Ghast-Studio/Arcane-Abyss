package net.headnutandpasci.arcaneabyss;

import com.google.common.collect.Lists;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.client.misc.YallaRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.BlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.DarkBlueSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.blue.SlimePillarRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.boss.black.BlackSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.boss.slimeviathan.SlimeviathanRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.green.GreenSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.DarkRedSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.RedSlimeRenderer;
import net.headnutandpasci.arcaneabyss.entity.client.slime.red.RedSlimeStationaryRenderer;
import net.headnutandpasci.arcaneabyss.networking.MovementControlPacket;
import net.headnutandpasci.arcaneabyss.recipe.SlimeSteelRecipe;
import net.headnutandpasci.arcaneabyss.screen.ModScreenHandlers;
import net.headnutandpasci.arcaneabyss.screen.SlimeSteelMachineScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ArcaneAbyssModClient implements ClientModInitializer {

    public static final RecipeBookGroup MAIN_GROUP = RecipeBookHelper.createGroup(new Identifier(ArcaneAbyss.MOD_ID, "slime_steel_machine/main"), Items.SLIME_BALL.getDefaultStack());
    public static final RecipeBookGroup SEARCH_GROUP = RecipeBookHelper.createGroup(new Identifier(ArcaneAbyss.MOD_ID, "slime_steel_machine/search"), Items.COMPASS.getDefaultStack());

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.BLUE_SLIME, BlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIME_PILLAR, SlimePillarRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_SLIME, RedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_SLIME_STATIONARY, RedSlimeStationaryRenderer::new);
        EntityRendererRegistry.register(ModEntities.GREEN_SLIME, GreenSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_BLUE_SLIME, DarkBlueSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_RED_SLIME, DarkRedSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLACK_SLIME, BlackSlimeRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIMEVIATHAN, SlimeviathanRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIME_PROJECTILE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.YALLA, YallaRenderer::new);

        RecipeBookHelper.registerAndAddToSearch(ArcaneAbyss.SLIME_STEEL_CATEGORY, SEARCH_GROUP, Lists.newArrayList(SEARCH_GROUP, MAIN_GROUP));
        RecipeBookHelper.registerGroupLookup(SlimeSteelRecipe.Type.INSTANCE, recipe -> MAIN_GROUP);

        HandledScreens.register(ModScreenHandlers.SLIMESTEEL_SCREEN_HANDLER, SlimeSteelMachineScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(MovementControlPacket.ID, MovementControlPacket::handle);
    }

}
