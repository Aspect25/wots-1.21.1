package net.wots.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.network.SetUziVariantPayload;

public class UziVariantWheelScreen extends AbstractVariantWheelScreen {
    private static final UziPlushVariant[] V = UziPlushVariant.values();
    public UziVariantWheelScreen(BlockPos pos) { super(pos); }
    @Override protected int variantCount()              { return V.length; }
    @Override protected String variantDisplayName(int i) { return V[i].displayName; }
    @Override protected int variantColor(int i)          { return V[i].color; }
    @Override protected String variantEnumName(int i)    { return V[i].name(); }
    @Override protected String characterId()             { return "uzi"; }
    @Override protected void sendPayload(BlockPos pos, String name, boolean lazy) {
        ClientPlayNetworking.send(new SetUziVariantPayload(pos, name, lazy));
    }
}
