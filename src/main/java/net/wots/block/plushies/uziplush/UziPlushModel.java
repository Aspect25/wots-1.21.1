package net.wots.block.plushies.uziplush;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.wots.block.entity.UziPlushBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class UziPlushModel extends GeoModel<UziPlushBlockEntity> {

    @Override
    public Identifier getModelResource(UziPlushBlockEntity entity) {
        return Identifier.of("wots", "geo/uzi_plush.geo.json");
    }

    @Override
    public Identifier getTextureResource(UziPlushBlockEntity entity) {
        // Gets the block's registry name e.g. "uzi_plush_sadge"
        String blockName = Registries.BLOCK.getId(entity.getCachedState().getBlock()).getPath();
        return Identifier.of("wots", "textures/block/" + blockName + ".png");
    }
    @Override
    public Identifier getAnimationResource(UziPlushBlockEntity entity) {
        return Identifier.of("wots", "animations/uzi_plush.animation.json");
    }
}