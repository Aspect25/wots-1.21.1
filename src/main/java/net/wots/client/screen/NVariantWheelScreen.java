package net.wots.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.network.SetNVariantPayload;

public class NVariantWheelScreen extends AbstractVariantWheelScreen {
    private static final NPlushVariant[] V = NPlushVariant.values();
    public NVariantWheelScreen(BlockPos pos) { super(pos); }
    @Override protected int variantCount()              { return V.length; }
    @Override protected String variantDisplayName(int i) { return V[i].displayName; }
    @Override protected int variantColor(int i)          { return V[i].color; }
    @Override protected String variantEnumName(int i)    { return V[i].name(); }
    @Override protected String characterId()             { return "n"; }
    @Override protected void sendPayload(BlockPos pos, String name, boolean lazy) {
        ClientPlayNetworking.send(new SetNVariantPayload(pos, name, lazy));
    }
}
