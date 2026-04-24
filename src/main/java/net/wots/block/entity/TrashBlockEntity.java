package net.wots.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.block.ModBlocks;
import net.wots.sound.ModSounds;

import java.util.Map;

import static net.wots.util.ModTags.Items.TRACKED_TRASH_ITEMS;

public class TrashBlockEntity extends BlockEntity implements Container {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    // Tag for items that trigger the special counter
    public record ThresholdSound(SoundEvent sound, int pauseDuration) {}
    public static final Map<Integer, ThresholdSound> THRESHOLD_SOUNDS = Map.ofEntries(
            // First 4 plushies: one line each
            Map.entry(1,  new ThresholdSound(ModSounds.GABE_LINE_1,  260)),  // 13s
            Map.entry(2,  new ThresholdSound(ModSounds.GABE_LINE_2,  380)),  // 19s
            Map.entry(3,  new ThresholdSound(ModSounds.GABE_LINE_3,  320)),  // 16s
            Map.entry(4,  new ThresholdSound(ModSounds.GABE_LINE_4,  320)),  // 16s
            // Then every 5 plushies
            Map.entry(5,  new ThresholdSound(ModSounds.GABE_LINE_5,  240)),  // 12s
            Map.entry(10, new ThresholdSound(ModSounds.GABE_LINE_6,  80)),   // 4s
            Map.entry(15, new ThresholdSound(ModSounds.GABE_LINE_7,  80)),   // 4s
            Map.entry(20, new ThresholdSound(ModSounds.GABE_LINE_8,  260)),  // 13s
            Map.entry(25, new ThresholdSound(ModSounds.GABE_LINE_9,  220)),  // 11s
            Map.entry(30, new ThresholdSound(ModSounds.GABE_LINE_10, 380)),  // 19s
            Map.entry(35, new ThresholdSound(ModSounds.GABE_LINE_11, 360)),  // 18s
            Map.entry(40, new ThresholdSound(ModSounds.GABE_LINE_12, 140)),  // 7s
            Map.entry(45, new ThresholdSound(ModSounds.GABE_LINE_13, 60)),   // 3s
            Map.entry(50, new ThresholdSound(ModSounds.GABE_LINE_14, 60)),   // 3s
            Map.entry(55, new ThresholdSound(ModSounds.GABE_LINE_15, 40)),   // 2s
            Map.entry(60, new ThresholdSound(ModSounds.GABE_LINE_16, 60)),   // 3s
            Map.entry(65, new ThresholdSound(ModSounds.GABE_LINE_17, 40)),   // 2s
            Map.entry(70, new ThresholdSound(ModSounds.GABE_LINE_18, 640)),  // 32s
            Map.entry(75, new ThresholdSound(ModSounds.GABE_LINE_19, 60)),   // 3s
            Map.entry(80, new ThresholdSound(ModSounds.GABE_LINE_20, 340))   // 17s
    );


    // Pause duration in ticks (e.g., 100 ticks = 5 seconds)


    private int progress = 0;
    private int specialItemsDeleted = 0;
    private int pauseTicks = 0;

    public TrashBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TRASH_BLOCK_ENTITY, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TrashBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.pauseTicks > 0) {
            entity.pauseTicks--;
            return; // Paused, don't delete items
        }

        entity.progress++;
        if (entity.progress >= 2) { // 1 item per 2 ticks (10 items per second)
            entity.progress = 0;
            ItemStack currentStack = entity.inventory.get(0);

            if (!currentStack.isEmpty()) {
                // Check if it's a tracked item
                boolean isTracked = currentStack.is(TRACKED_TRASH_ITEMS);

                // Delete 1 item
                ItemStack deleted = currentStack.split(1);

                if (isTracked) {
                    entity.specialItemsDeleted++;
                    entity.checkThresholds(level, pos);
                }

                if (currentStack.isEmpty()) {
                    entity.inventory.set(0, ItemStack.EMPTY);
                }

                entity.setChanged();
            }
        }
    }

    private void checkThresholds(Level level, BlockPos pos) {
        ThresholdSound entry = THRESHOLD_SOUNDS.get(this.specialItemsDeleted);
        if (entry != null) {
            level.playSound(null, pos, entry.sound(), SoundSource.BLOCKS, 1.0f, 1.0f);
            this.pauseTicks = entry.pauseDuration();
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("TrashProgress", progress);
        output.putInt("SpecialItemsDeleted", specialItemsDeleted);
        output.putInt("PauseTicks", pauseTicks);

        ItemStack stack = inventory.get(0);
        if (!stack.isEmpty()) {
            output.storeNullable("InventoryItem", ItemStack.CODEC, stack);
        }
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        progress = input.getIntOr("TrashProgress", 0);
        specialItemsDeleted = input.getIntOr("SpecialItemsDeleted", 0);
        pauseTicks = input.getIntOr("PauseTicks", 0);

        inventory.set(0, input.read("InventoryItem", ItemStack.CODEC).orElse(ItemStack.EMPTY));
    }

    // Container implementation
    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = inventory.get(slot).split(amount);
        if (inventory.get(slot).isEmpty()) {
            inventory.set(slot, ItemStack.EMPTY);
        }
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        setChanged();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inventory.clear();
        setChanged();
    }
}
