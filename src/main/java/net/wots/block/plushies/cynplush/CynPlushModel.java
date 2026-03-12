package net.wots.block.plushies.cynplush;


import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.entity.UziPlushBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class CynPlushModel extends GeoModel<CynPlushBlockEntity> {

    @Override
    public Identifier getModelResource(CynPlushBlockEntity entity) {
        return Identifier.of("wots", "geo/cyn_plush.geo.json");
    }

    @Override
    public Identifier getTextureResource(CynPlushBlockEntity entity) {
        // Gets the block's registry name e.g. "uzi_plush_sadge"
        String blockName = Registries.BLOCK.getId(entity.getCachedState().getBlock()).getPath();
        return Identifier.of("wots", "textures/block/" + blockName + ".png");
    }
    @Override
    public Identifier getAnimationResource(CynPlushBlockEntity entity) {
        return Identifier.of("wots", "animations/cyn_plush.animation.json");
    }
}