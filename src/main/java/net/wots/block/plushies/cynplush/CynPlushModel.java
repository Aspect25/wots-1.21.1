package net.wots.block.plushies.cynplush;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.wots.block.entity.CynPlushBlockEntity;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class CynPlushModel extends GeoModel<CynPlushBlockEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath("wots", "cyn_plush");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO: In GeckoLib 5, need to get entity from GeoRenderState to look up block name.
        // For now, return the default cyn_plush texture.
        return Identifier.fromNamespaceAndPath("wots", "textures/block/cyn_plush.png");
    }

    @Override
    public Identifier getAnimationResource(CynPlushBlockEntity entity) {
        return Identifier.fromNamespaceAndPath("wots", "cyn_plush");
    }
}
