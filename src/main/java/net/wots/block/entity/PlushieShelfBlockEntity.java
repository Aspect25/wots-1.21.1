package net.wots.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.wots.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class PlushieShelfBlockEntity extends BlockEntity {

    public static final int SLOTS = 5;
    private final ItemStack[] plushies = new ItemStack[SLOTS];

    private static final int BOUNCE_COOLDOWN_TICKS = 20;
    private final long[] lastBounceTicks = new long[SLOTS];

    // ── Transient bounce trigger (not saved to world, only sent in update packets) ──
    // Set to the slot index before calling level.sendBlockUpdated(), then reset to -1.
    // The client reads this in loadAdditional() and triggers the GeckoLib anim on the proxy.
    private int bounceSlot = -1;

    // ── Client-side animation proxies ─────────────────────────────────────────
    // One NPlushBlockEntity per slot, never added to the world.
    // They exist solely so GeckoLib has a GeoBlockEntity instance whose
    // AnimatableInstanceCache can track and play the "bounce" animation.
    // Your PlushieShelfBlockEntityRenderer should call getAnimProxy(slot) and
    // pass it as the animatable when rendering each NPlush model on the shelf.
    @Nullable
    private NPlushBlockEntity[] animProxies = null;

    public NPlushBlockEntity getAnimProxy(int slot) {
        if (animProxies == null) {
            animProxies = new NPlushBlockEntity[SLOTS];
            for (int i = 0; i < SLOTS; i++) {
                // Constructed without a world - GeckoLib only needs the cache, not the world.
                animProxies[i] = new NPlushBlockEntity(getBlockPos(), getBlockState());
            }
        }
        return animProxies[slot];
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public PlushieShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PLUSHIE_SHELF_BLOCK_ENTITY, pos, state);
        for (int i = 0; i < SLOTS; i++) plushies[i] = ItemStack.EMPTY;
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    public ItemStack getPlushie(int slot) { return plushies[slot]; }
    public boolean isEmpty(int slot)      { return plushies[slot].isEmpty(); }

    public int firstEmpty() {
        for (int i = 0; i < SLOTS; i++) if (isEmpty(i)) return i;
        return -1;
    }

    public boolean place(int slot, ItemStack stack) {
        if (!isEmpty(slot)) return false;
        plushies[slot] = stack.copyWithCount(1);
        sync();
        return true;
    }

    public ItemStack take(int slot) {
        ItemStack out = plushies[slot].copy();
        plushies[slot] = ItemStack.EMPTY;
        sync();
        return out;
    }

    // ── Animation bounce ──────────────────────────────────────────────────────

    /**
     * Called server-side when a player interacts with a shelf slot.
     * Sends an update packet to nearby clients with bounceSlot set so they can
     * trigger the GeckoLib "bounce" animation on the correct proxy.
     */
    public void triggerBounce(int slot) {
        if (level == null || level.isClientSide()) return;
        long now = level.getGameTime();
        if (now - lastBounceTicks[slot] < BOUNCE_COOLDOWN_TICKS) return;
        lastBounceTicks[slot] = now;
        bounceSlot = slot;
        // sendBlockUpdated creates the packet synchronously (calls getUpdatePacket ->
        // saveWithFullMetadata -> saveAdditional) while bounceSlot is still set.
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        // Reset immediately so world-save NBT never persists a stale slot number.
        bounceSlot = -1;
    }

    // ── Sync ──────────────────────────────────────────────────────────────────

    /** Marks dirty AND pushes update packet to all nearby clients. */
    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < SLOTS; i++) {
            if (!plushies[i].isEmpty()) {
                output.storeNullable("Slot" + i, ItemStack.CODEC, plushies[i]);
            }
        }
        if (bounceSlot >= 0) {
            output.putInt("bounceSlot", bounceSlot);
        }
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOTS; i++) {
            plushies[i] = input.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        }
        int slot = input.getIntOr("bounceSlot", -1);
        if (level != null && level.isClientSide() && slot >= 0 && slot < SLOTS) {
            getAnimProxy(slot).triggerAnim("controller", "bounce");
        }
    }
}
