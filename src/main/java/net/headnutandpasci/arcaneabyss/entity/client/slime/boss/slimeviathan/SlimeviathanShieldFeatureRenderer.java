package net.headnutandpasci.arcaneabyss.entity.client.slime.boss.slimeviathan;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

public class SlimeviathanShieldFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<SlimeviathanEntity, SlimeEntityModel<SlimeviathanEntity>> {
    private static final Identifier SKIN = new Identifier(ArcaneAbyss.MOD_ID, "textures/entity/slime/boss/armor.png");
    private final SlimeEntityModel<SlimeviathanEntity> model;

    public SlimeviathanShieldFeatureRenderer(FeatureRendererContext<SlimeviathanEntity, SlimeEntityModel<SlimeviathanEntity>> context, EntityModelLoader loader) {
        super(context);
        this.model = new SlimeEntityModel<>(loader.getModelPart(EntityModelLayers.SLIME_OUTER));
    }

    @Override
    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.01F;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    @Override
    protected EntityModel<SlimeviathanEntity> getEnergySwirlModel() {
        return this.model;
    }
}
