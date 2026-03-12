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

    public PlushieShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PLUSHIE_SHELF_BLOCK_ENTITY, pos, state);
        for (int i = 0; i < SLOTS; i++) plushies[i] = ItemStack.EMPTY;
    }

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

    /** Marks dirty AND pushes update packet to all nearby clients. */
    private void sync() {
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // ── These two methods are what actually send data to the client ───────────
    // toUpdatePacket()        → called when world.updateListeners() fires
    // toInitialChunkDataNbt() → called when a player first loads the chunk

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
        NbtList list = new NbtList();
        for (int i = 0; i < SLOTS; i++) {
            NbtCompound entry = new NbtCompound();
            entry.putInt("slot", i);
            if (!plushies[i].isEmpty())
                entry.put("item", plushies[i].encode(registries));
            list.add(entry);
        }
        nbt.put("plushies", list);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        for (int i = 0; i < SLOTS; i++) plushies[i] = ItemStack.EMPTY;
        NbtList list = nbt.getList("plushies", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            int slot = entry.getInt("slot");
            if (slot >= 0 && slot < SLOTS && entry.contains("item"))
                plushies[slot] = ItemStack.fromNbtOrEmpty(registries, entry.getCompound("item"));
        }
    }
}