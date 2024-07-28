package net.headnutandpasci.arcaneabyss.entity.client.slime.boss.black;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlackSlimeShieldFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<BlackSlimeEntity, SlimeEntityModel<BlackSlimeEntity>> {
    private static final Identifier SKIN = new Identifier(ArcaneAbyss.MOD_ID, "textures/entity/blue.png");
    private final SlimeEntityModel<BlackSlimeEntity> model;

    public BlackSlimeShieldFeatureRenderer(FeatureRendererContext<BlackSlimeEntity, SlimeEntityModel<BlackSlimeEntity>> context, EntityModelLoader loader) {
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
    protected EntityModel<BlackSlimeEntity> getEnergySwirlModel() {
        return this.model;
    }
}
