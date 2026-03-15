package net.wots.block.luminite;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.List;
import java.util.function.Supplier;

/**
 * Wraps the lit Luminite model with a second emissive pass.
 *
 * Pass 1: base model rendered normally (affected by block light).
 * Pass 2: emissive overlay (_e texture) rendered at full brightness,
 *         completely ignoring the surrounding light level.
 *
 * This means the glowing core of the crystal always looks lit up even
 * in total darkness — like a sea lantern or nether portal frame.
 */
public class LuminiteEmissiveModel implements BakedModel, FabricBakedModel {

    private final BakedModel base;
    private final BakedModel emissiveOverlay;

    // Lazily initialised so we don't crash if Fabric Renderer isn't ready yet
    private static RenderMaterial emissiveMaterial = null;

    public LuminiteEmissiveModel(BakedModel base, BakedModel emissiveOverlay) {
        this.base             = base;
        this.emissiveOverlay  = emissiveOverlay;
    }

    private static RenderMaterial emissiveMaterial() {
        if (emissiveMaterial == null && RendererAccess.INSTANCE.hasRenderer()) {
            emissiveMaterial = RendererAccess.INSTANCE.getRenderer()
                .materialFinder()
                .emissive(true)
                .find();
        }
        return emissiveMaterial;
    }

    // ── FabricBakedModel ──────────────────────────────────────────────────────

    @Override
    public boolean isVanillaAdapter() {
        return false; // tells Fabric to use emitBlockQuads instead of getQuads
    }

    @Override
    public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos,
                               Supplier<Random> random, RenderContext ctx) {
        // Pass 1 — normal shaded rendering
        ((FabricBakedModel) base).emitBlockQuads(world, state, pos, random, ctx);

        // Pass 2 — emissive overlay (always full brightness)
        RenderMaterial mat = emissiveMaterial();
        if (mat != null && emissiveOverlay != null) {
            ctx.pushTransform(quad -> {
                quad.material(mat);
                return true; // keep all quads
            });
            ((FabricBakedModel) emissiveOverlay).emitBlockQuads(world, state, pos, random, ctx);
            ctx.popTransform();
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> random, RenderContext ctx) {
        // Items just use the base model — emissive looks fine on items without the pass
        ((FabricBakedModel) base).emitItemQuads(stack, random, ctx);
    }

    // ── BakedModel delegates ──────────────────────────────────────────────────

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        return base.getQuads(state, face, random);
    }

    @Override public boolean useAmbientOcclusion()  { return false; } // no AO — we handle brightness
    @Override public boolean hasDepth()              { return base.hasDepth(); }
    @Override public boolean isSideLit()             { return false; }
    @Override public boolean isBuiltin()             { return base.isBuiltin(); }
    @Override public Sprite getParticleSprite()      { return base.getParticleSprite(); }
    @Override public ModelTransformation getTransformation() { return base.getTransformation(); }
    @Override public ModelOverrideList getOverrides()        { return base.getOverrides(); }
}
