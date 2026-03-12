package net.wots.block.plushies.tadc.sigma;

import net.minecraft.util.Identifier;
import net.wots.block.entity.SigmaBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class SigmaBlockModel extends GeoModel<SigmaBlockEntity> {

    @Override
    public Identifier getModelResource(SigmaBlockEntity animatable) {
        return Identifier.of("wots", "geo/sigma.geo.json");
    }

    @Override
    public Identifier getTextureResource(SigmaBlockEntity animatable) {
        return Identifier.of("wots", "textures/block/sigma.png");
    }

    @Override
    public Identifier getAnimationResource(SigmaBlockEntity animatable) {
        return Identifier.of("wots", "animations/sigma.animation.json");
    }
}

