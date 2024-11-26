package net.headnutandpasci.arcaneabyss.entity.client.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.headnutandpasci.arcaneabyss.entity.model.YallaEntityModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class YallaRenderer extends MobEntityRenderer<YallaEntity, YallaEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/allay/allay.png");

    public YallaRenderer(EntityRendererFactory.Context context) {
        super(context, new YallaEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4F);
        this.addFeature(new HeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
    }

    public Identifier getTexture(YallaEntity allayEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
    }

