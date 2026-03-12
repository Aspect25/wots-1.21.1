package net.wots.block.plushies.nplush;

import net.minecraft.registry.Registries;
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
        String blockName = Registries.BLOCK.getId(entity.getCachedState().getBlock()).getPath();
        return Identifier.of("wots", "textures/block/" + blockName + ".png");
    }

    @Override
    public Identifier getAnimationResource(NPlushBlockEntity entity) {
        return Identifier.of("wots", "animations/n_plush.animation.json");
    }
}