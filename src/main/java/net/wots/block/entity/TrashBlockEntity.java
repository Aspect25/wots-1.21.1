package net.wots.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;
import net.minecraft.item.Item;
import net.wots.block.ModBlocks;
import net.wots.sound.ModSounds;

import java.util.Map;

import static net.wots.util.ModTags.Items.TRACKED_TRASH_ITEMS;

public class TrashBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    
    // Tag for items that trigger the special counter
    public record ThresholdSound(SoundEvent sound, int pauseDuration) {}
    public static final Map<Integer, ThresholdSound> THRESHOLD_SOUNDS = Map.ofEntries(
            Map.entry(1,  new ThresholdSound(ModSounds.TRASHED_1,   100)),
            Map.entry(5,  new ThresholdSound(ModSounds.TRASHED_5,   120)),  // was duplicate 10
            Map.entry(10, new ThresholdSound(ModSounds.TRASHED_10,  80)),
            Map.entry(15, new ThresholdSound(ModSounds.TRASHED_15,  80)),
            Map.entry(20, new ThresholdSound(ModSounds.TRASHED_20,  120)),
            Map.entry(25, new ThresholdSound(ModSounds.TRASHED_25,  80)),
            Map.entry(30, new ThresholdSound(ModSounds.TRASHED_30,  60)),
            Map.entry(35, new ThresholdSound(ModSounds.TRASHED_35,  240)),
            Map.entry(40, new ThresholdSound(ModSounds.TRASHED_40,  100)),
            Map.entry(50, new ThresholdSound(ModSounds.TRASHED_50,  240)),
            Map.entry(69, new ThresholdSound(ModSounds.TRASHED_69,  140)),
            Map.entry(70, new ThresholdSound(ModSounds.TRASHED_70,  840))
    );

    
    // Pause duration in ticks (e.g., 100 ticks = 5 seconds)


    private int progress = 0;
    private int specialItemsDeleted = 0;
    private int pauseTicks = 0;

    public TrashBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TRASH_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TrashBlockEntity entity) {
        if (world.isClient) return;

        if (entity.pauseTicks > 0) {
            entity.pauseTicks--;
            return; // Paused, don't delete items
        }

        entity.progress++;
        if (entity.progress >= 20) { // 1 item per second (20 ticks)
            entity.progress = 0;
            ItemStack currentStack = entity.inventory.get(0);
            
            if (!currentStack.isEmpty()) {
                // Check if it's a tracked item
                boolean isTracked = currentStack.isIn(TRACKED_TRASH_ITEMS);
                
                // Delete 1 item
                ItemStack deleted = currentStack.split(1);
                
                if (isTracked) {
                    entity.specialItemsDeleted++;
                    entity.checkThresholds(world, pos);
                }
                
                if (currentStack.isEmpty()) {
                    entity.inventory.set(0, ItemStack.EMPTY);
                }
                
                entity.markDirty();
            }
        }
    }

    private void checkThresholds(World world, BlockPos pos) {
        ThresholdSound entry = THRESHOLD_SOUNDS.get(this.specialItemsDeleted);
        if (entry != null) {
            world.playSound(null, pos, entry.sound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.pauseTicks = entry.pauseDuration();
            this.markDirty();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("TrashProgress", progress);
        nbt.putInt("SpecialItemsDeleted", specialItemsDeleted);
        nbt.putInt("PauseTicks", pauseTicks);

        // Properly serialize ItemStack for Fabric 1.21.1
        ItemStack stack = inventory.get(0);
        if (!stack.isEmpty()) {
            NbtCompound itemNbt = (NbtCompound) stack.encodeAllowEmpty(registries);
            nbt.put("InventoryItem", itemNbt);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        progress = nbt.getInt("TrashProgress");
        specialItemsDeleted = nbt.getInt("SpecialItemsDeleted");
        pauseTicks = nbt.getInt("PauseTicks");

        if (nbt.contains("InventoryItem")) {
            ItemStack stack = ItemStack.fromNbtOrEmpty(registries, nbt.getCompound("InventoryItem"));
            inventory.set(0, stack);
        } else {
            inventory.set(0, ItemStack.EMPTY);
        }
    }

    // Inventory implementation
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = inventory.get(slot).split(amount);
        if (inventory.get(slot).isEmpty()) {
            inventory.set(slot, ItemStack.EMPTY);
        }
        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }
}
