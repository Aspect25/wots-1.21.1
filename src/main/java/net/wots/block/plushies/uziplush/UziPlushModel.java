package net.wots.block.plushies.uziplush;

import net.minecraft.util.Identifier;
import net.wots.block.entity.UziPlushBlockEntity;
import software.bernie.geckolib.model.GeoModel;

import java.util.Locale;

public class UziPlushModel extends GeoModel<UziPlushBlockEntity> {

    @Override
    public Identifier getModelResource(UziPlushBlockEntity entity) {
        return Identifier.of("wots", "geo/uzi_plush.geo.json");
    }

    @Override
    public Identifier getTextureResource(UziPlushBlockEntity entity) {
        String textureName = entity.getVariant().name().toLowerCase(Locale.ROOT);
        return Identifier.of("wots", "textures/block/" + textureName + ".png");
    }

    @Override
    public Identifier getAnimationResource(UziPlushBlockEntity entity) {
        return Identifier.of("wots", "animations/uzi_plush.animation.json");
    }
}