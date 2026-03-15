package net.wots.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.wots.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class PlushieShelfBlockEntity extends BlockEntity {

    public static final int SLOTS = 5;
    private final ItemStack[] plushies = new ItemStack[SLOTS];

    // ── Transient bounce trigger (not saved to world, only sent in update packets) ──
    // Set to the slot index before calling world.updateListeners(), then reset to -1.
    // The client reads this in readNbt() and triggers the GeckoLib anim on the proxy.
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
                animProxies[i] = new NPlushBlockEntity(pos, getCachedState());
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
        if (world == null || world.isClient) return;
        bounceSlot = slot;
        // updateListeners creates the packet synchronously (calls toUpdatePacket ->
        // createNbt -> writeNbt) while bounceSlot is still set.
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        // Reset immediately so world-save NBT never persists a stale slot number.
        bounceSlot = -1;
    }

    // ── Sync ──────────────────────────────────────────────────────────────────

    /** Marks dirty AND pushes update packet to all nearby clients. */
    private void sync() {
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        // Plushie item stacks (always written for save + sync)
        NbtList list = new NbtList();
        for (int i = 0; i < SLOTS; i++) {
            NbtCompound entry = new NbtCompound();
            entry.putInt("slot", i);
            if (!plushies[i].isEmpty())
                entry.put("item", plushies[i].encode(registries));
            list.add(entry);
        }
        nbt.put("plushies", list);

        // Bounce trigger (only written when a bounce is pending; -1 is the default
        // so we omit it from world saves to keep NBT clean)
        if (bounceSlot >= 0) {
            nbt.putInt("bounceSlot", bounceSlot);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        // Plushies
        for (int i = 0; i < SLOTS; i++) plushies[i] = ItemStack.EMPTY;
        NbtList list = nbt.getList("plushies", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            int slot = entry.getInt("slot");
            if (slot >= 0 && slot < SLOTS && entry.contains("item"))
                plushies[slot] = ItemStack.fromNbtOrEmpty(registries, entry.getCompound("item"));
        }

        // Bounce trigger — only present in update packets, never in world-save data.
        // On the client, fire the GeckoLib animation on the correct proxy.
        if (nbt.contains("bounceSlot")) {
            int slot = nbt.getInt("bounceSlot");
            if (world != null && world.isClient && slot >= 0 && slot < SLOTS) {
                getAnimProxy(slot).triggerAnim("controller", "bounce");
            }
        }
    }
}