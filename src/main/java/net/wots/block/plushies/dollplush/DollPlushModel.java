package net.wots.block.plushies.dollplush;


import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.entity.DollPlushBlockEntity;
import net.wots.block.entity.UziPlushBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class DollPlushModel extends GeoModel<DollPlushBlockEntity> {

    @Override
    public Identifier getModelResource(DollPlushBlockEntity entity) {
        return Identifier.of("wots", "geo/doll_plush.geo.json");
    }

    @Override
    public Identifier getTextureResource(DollPlushBlockEntity entity) {
        // Gets the block's registry name e.g. "uzi_plush_sadge"
        String blockName = Registries.BLOCK.getId(entity.getCachedState().getBlock()).getPath();
        return Identifier.of("wots", "textures/block/" + blockName + ".png");
    }
    @Override
    public Identifier getAnimationResource(DollPlushBlockEntity entity) {
        return Identifier.of("wots", "animations/doll_plush.animation.json");
    }
}