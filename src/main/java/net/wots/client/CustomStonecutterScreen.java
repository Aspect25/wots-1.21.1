package net.wots.client;

import net.wots.recipe.CustomStonecuttingRecipe;
import net.wots.screen.CustomStonecutterScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class CustomStonecutterScreen extends HandledScreen<CustomStonecutterScreenHandler> {

    // Main background texture (your 176x166 PNG)
    private static final Identifier TEXTURE =
            Identifier.of("wots", "textures/gui/container/plushie_workshop.png");

    // GUI sprites — place these at:
    // assets/wots/textures/gui/sprites/container/plushie_workshop/recipe.png           (16x18)
    // assets/wots/textures/gui/sprites/container/plushie_workshop/recipe_highlighted.png (16x18)
    // assets/wots/textures/gui/sprites/container/plushie_workshop/recipe_selected.png   (16x18)
    // assets/wots/textures/gui/sprites/container/plushie_workshop/scroller.png          (12x15)
    // assets/wots/textures/gui/sprites/container/plushie_workshop/scroller_disabled.png (12x15)
    private static final Identifier SCROLLER_TEXTURE =
            Identifier.of("wots", "container/plushie_workshop/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE =
            Identifier.of("wots", "container/plushie_workshop/scroller_disabled");
    private static final Identifier RECIPE_TEXTURE =
            Identifier.of("wots", "container/plushie_workshop/recipe");
    private static final Identifier RECIPE_HIGHLIGHTED_TEXTURE =
            Identifier.of("wots", "container/plushie_workshop/recipe_highlighted");
    private static final Identifier RECIPE_SELECTED_TEXTURE =
            Identifier.of("wots", "container/plushie_workshop/recipe_selected");

    // Layout constants — exactly matching vanilla
    private static final int RECIPE_LIST_COLUMNS    = 4;
    private static final int RECIPE_LIST_ROWS       = 3;
    private static final int RECIPE_ENTRY_WIDTH     = 16;
    private static final int RECIPE_ENTRY_HEIGHT    = 18;
    private static final int RECIPE_LIST_OFFSET_X   = 52;
    private static final int RECIPE_LIST_OFFSET_Y   = 14;

    private float scrollAmount  = 0.0f;
    private boolean mouseClicked = false;
    private int scrollOffset    = 0;
    private boolean canCraft    = false;

    public CustomStonecutterScreen(CustomStonecutterScreenHandler handler,
                                   PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Listen for inventory changes just like vanilla
        handler.setContentsChangedListener(this::onInventoryChange);
        this.titleY--;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;

        // Main GUI background
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        // Scrollbar thumb — disabled texture when can't scroll
        int k = (int)(41.0f * this.scrollAmount);
        Identifier scrollTex = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.drawGuiTexture(scrollTex, i + 119, j + 15 + k, 12, 15);

        // Recipe list
        int l = this.x + RECIPE_LIST_OFFSET_X;
        int m = this.y + RECIPE_LIST_OFFSET_Y;
        int n = this.scrollOffset + 12;
        renderRecipeBackground(context, mouseX, mouseY, l, m, n);
        renderRecipeIcons(context, l, m, n);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        super.drawMouseoverTooltip(context, x, y);
        if (this.canCraft) {
            int i = this.x + RECIPE_LIST_OFFSET_X;
            int j = this.y + RECIPE_LIST_OFFSET_Y;
            int k = this.scrollOffset + 12;
            List<RecipeEntry<CustomStonecuttingRecipe>> list = this.handler.getAvailableRecipes();

            for (int l = this.scrollOffset; l < k && l < this.handler.getAvailableRecipeCount(); l++) {
                int m = l - this.scrollOffset;
                int n = i + m % 4 * 16;
                int o = j + m / 4 * 18 + 2;
                if (x >= n && x < n + 16 && y >= o && y < o + 18) {
                    context.drawItemTooltip(this.textRenderer,
                            list.get(l).value().getResult(this.client.world.getRegistryManager()), x, y);
                }
            }
        }
    }

    private void renderRecipeBackground(DrawContext context, int mouseX, int mouseY, int x, int y, int scrollOffset) {
        for (int i = this.scrollOffset; i < scrollOffset && i < this.handler.getAvailableRecipeCount(); i++) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 16;
            int l = j / 4;
            int m = y + l * 18 + 2;

            Identifier tex;
            if (i == this.handler.getSelectedRecipe()) {
                tex = RECIPE_SELECTED_TEXTURE;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                tex = RECIPE_HIGHLIGHTED_TEXTURE;
            } else {
                tex = RECIPE_TEXTURE;
            }

            context.drawGuiTexture(tex, k, m - 1, 16, 18);
        }
    }

    private void renderRecipeIcons(DrawContext context, int x, int y, int scrollOffset) {
        List<RecipeEntry<CustomStonecuttingRecipe>> list = this.handler.getAvailableRecipes();

        for (int i = this.scrollOffset; i < scrollOffset && i < this.handler.getAvailableRecipeCount(); i++) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 16;
            int l = j / 4;
            int m = y + l * 18 + 2;
            context.drawItem(list.get(i).value().getResult(this.client.world.getRegistryManager()), k, m);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
        if (this.canCraft) {
            int i = this.x + RECIPE_LIST_OFFSET_X;
            int j = this.y + RECIPE_LIST_OFFSET_Y;
            int k = this.scrollOffset + 12;

            for (int l = this.scrollOffset; l < k; l++) {
                int m = l - this.scrollOffset;
                double d = mouseX - (i + m % 4 * 16);
                double e = mouseY - (j + m / 4 * 18);
                if (d >= 0.0 && e >= 0.0 && d < 16.0 && e < 18.0
                        && this.handler.onButtonClick(this.client.player, l)) {
                    MinecraftClient.getInstance().getSoundManager().play(
                            PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                    this.client.interactionManager.clickButton(this.handler.syncId, l);
                    return true;
                }
            }

            // Scrollbar click area
            int si = this.x + 119;
            int sj = this.y + 9;
            if (mouseX >= si && mouseX < si + 12 && mouseY >= sj && mouseY < sj + 54) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int i = this.y + 14;
            int j = i + 54;
            this.scrollAmount = ((float)mouseY - i - 7.5f) / (j - i - 15.0f);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)(this.scrollAmount * this.getMaxScroll() + 0.5) * 4;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            float f = (float)verticalAmount / i;
            this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0f, 1.0f);
            this.scrollOffset = (int)(this.scrollAmount * i + 0.5) * 4;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && this.handler.getAvailableRecipeCount() > 12;
    }

    private int getMaxScroll() {
        return (this.handler.getAvailableRecipeCount() + 4 - 1) / 4 - 3;
    }

    private void onInventoryChange() {
        this.canCraft = this.handler.canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0f;
            this.scrollOffset = 0;
        }
    }
}