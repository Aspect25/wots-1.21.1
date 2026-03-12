package net.wots.block.plushies.uziplush;

import net.minecraft.util.Identifier;
import net.wots.block.entity.UziHugeBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class UziHugeModel extends GeoModel<UziHugeBlockEntity> {

    @Override
    public Identifier getModelResource(UziHugeBlockEntity entity) {
        // A new Blockbench model scaled to 3x3x3
        return Identifier.of("wots", "geo/uzi_plush_huge.geo.json");
    }

    @Override
    public Identifier getTextureResource(UziHugeBlockEntity entity) {
        // Reuse the same texture
        return Identifier.of("wots", "textures/block/uzi_plush.png");
    }

    @Override
    public Identifier getAnimationResource(UziHugeBlockEntity entity) {
        // Reuse the same animations
        return Identifier.of("wots", "animations/uzi_plush.animation.json");
    }
}