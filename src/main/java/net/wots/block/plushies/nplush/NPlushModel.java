package net.wots.block.plushies.nplush;

import net.minecraft.util.Identifier;
import net.wots.block.entity.NPlushBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class NPlushModel extends GeoModel<NPlushBlockEntity> {

    @Override
    public Identifier getModelResource(NPlushBlockEntity entity) {
        return Identifier.of("wots", "geo/n_plush.geo.json");
    }

    @Override
    public Identifier getTextureResource(NPlushBlockEntity entity) {
        String textureName = entity.getVariant().name().toLowerCase();
        return Identifier.of("wots", "textures/block/" + textureName + ".png");
    }

    @Override
    public Identifier getAnimationResource(NPlushBlockEntity entity) {
        return Identifier.of("wots", "animations/n_plush.animation.json");
    }
}