package net.wots.block.luminite;

/**
 * Wraps the lit Luminite model with a second emissive pass.
 *
 * TODO: MC 26.1 removed the old BakedModel / FabricBakedModel / RendererAccess
 * rendering pipeline. The Fabric Rendering API v1 no longer has the
 * net.fabricmc.fabric.api.renderer.v1 package (it was replaced by the
 * net.fabricmc.fabric.api.client.rendering.v1.FabricModel interface and
 * the new submit-based rendering system).
 *
 * This class needs to be completely rewritten to use the new model wrapping
 * approach in MC 26.1. For now it is stubbed out so the mod compiles.
 * The luminite block will render without the emissive overlay until this
 * is ported.
 */
public class LuminiteEmissiveModel {
    // Stubbed -- see TODO above
}
