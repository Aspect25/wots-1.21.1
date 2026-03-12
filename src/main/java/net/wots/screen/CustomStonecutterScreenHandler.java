package net.wots.screen;

import net.wots.Wots;
import net.wots.recipe.CustomStonecuttingRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.List;

public class CustomStonecutterScreenHandler extends ScreenHandler {

    public static final int INPUT_SLOT       = 0;
    public static final int OUTPUT_SLOT      = 1;
    public static final int PLAYER_INV_START = 2;
    public static final int PLAYER_INV_END   = 38;

    private final ScreenHandlerContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;

    private List<RecipeEntry<CustomStonecuttingRecipe>> availableRecipes = List.of();
    private ItemStack inputStack = ItemStack.EMPTY;
    public long lastTakeTime;

    // ── Listener so the Screen can react when inventory changes ──────────────
    private Runnable contentsChangedListener = () -> {};

    public void setContentsChangedListener(Runnable listener) {
        this.contentsChangedListener = listener;
    }

    private final Inventory input = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            CustomStonecutterScreenHandler.this.onContentChanged(this);
        }
    };

    private final Inventory output = new SimpleInventory(1);

    public CustomStonecutterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public CustomStonecutterScreenHandler(int syncId, PlayerInventory playerInventory,
                                          ScreenHandlerContext context) {
        super(Wots.CUSTOM_STONECUTTER_HANDLER, syncId);
        this.context = context;
        this.world   = playerInventory.player.getWorld();

        // Slot 0 — input
        this.addSlot(new Slot(input, 0, 20, 33));

        // Slot 1 — output
        this.addSlot(new Slot(output, 0, 143, 33) {
            @Override
            public boolean canInsert(ItemStack stack) { return false; }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.onCraftByPlayer(world, player, stack.getCount());
                output.setStack(0, ItemStack.EMPTY);

                ItemStack in = input.getStack(0);
                if (!in.isEmpty()) in.decrement(1);
                input.setStack(0, in);

                context.run((w, pos) -> {
                    long time = w.getTime();
                    if (lastTakeTime != time) {
                        w.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                                SoundCategory.BLOCKS, 1.0f, 1.0f);
                        lastTakeTime = time;
                    }
                });

                super.onTakeItem(player, stack);
            }
        });

        // Player inventory rows
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));

        // Hotbar
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));

        this.addProperty(selectedRecipe);
    }

    public int getSelectedRecipe() { return selectedRecipe.get(); }
    public List<RecipeEntry<CustomStonecuttingRecipe>> getAvailableRecipes() { return availableRecipes; }
    public int getAvailableRecipeCount() { return availableRecipes.size(); }

    // ── FIX: canCraft just checks if there are recipes available,
    //         NOT whether one is already selected — otherwise buttons
    //         won't appear at all when you first put an item in! ────────────
    public boolean canCraft() { return !availableRecipes.isEmpty(); }

    private boolean isValidRecipeIndex(int index) { return index >= 0 && index < availableRecipes.size(); }
    public ItemStack getOutputStack() { return output.getStack(0); }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, Wots.CUSTOM_STONECUTTER);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack stack = input.getStack(0);
        if (!stack.isOf(inputStack.getItem())) {
            inputStack = stack.copy();
            availableRecipes = List.of();
            selectedRecipe.set(-1);
            output.setStack(0, ItemStack.EMPTY);

            if (!stack.isEmpty()) {
                availableRecipes = world.getRecipeManager()
                        .getAllMatches(Wots.CUSTOM_STONECUTTING_TYPE,
                                new SingleStackRecipeInput(stack), world);
            }

            // Notify the screen so it can update canCraft and reset scroll
            contentsChangedListener.run();
        }
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        if (isValidRecipeIndex(id)) {
            selectedRecipe.set(id);
            updateResult();
            return true;
        }
        return false;
    }

    private void updateResult() {
        if (!availableRecipes.isEmpty() && isValidRecipeIndex(selectedRecipe.get())) {
            RecipeEntry<CustomStonecuttingRecipe> entry = availableRecipes.get(selectedRecipe.get());
            output.setStack(0, entry.value().craft(
                    new SingleStackRecipeInput(input.getStack(0)),
                    world.getRegistryManager()));
        } else {
            output.setStack(0, ItemStack.EMPTY);
        }
        sendContentUpdates();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (slotIndex == OUTPUT_SLOT) {
                stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
                if (!insertItem(stack, PLAYER_INV_START, PLAYER_INV_END, true)) return ItemStack.EMPTY;
                slot.onQuickTransfer(stack, result);
            } else if (slotIndex == INPUT_SLOT) {
                if (!insertItem(stack, PLAYER_INV_START, PLAYER_INV_END, false)) return ItemStack.EMPTY;
            } else {
                if (!insertItem(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();

            if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
            slot.onTakeItem(player, stack);
        }
        return result;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        output.setStack(0, ItemStack.EMPTY);
        context.run((world, pos) -> dropInventory(player, input));
    }
}