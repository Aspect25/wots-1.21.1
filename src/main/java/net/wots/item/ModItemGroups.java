package net.wots.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wots.Wots;
import net.wots.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup PLUSH_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Wots.MOD_ID, "plushies"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.UZI_PLUSH))
                    .displayName(Text.translatable("itemgroup.wots.plushies"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.UZI_PLUSH);
                        entries.add(ModBlocks.N_PLUSH);
                        entries.add(ModBlocks.CUSTOM_STONECUTTER);
                        entries.add(ModBlocks.PLUSHIE_SHELF);
                        entries.add(ModBlocks.TRASH_BLOCK);
                        entries.add(ModBlocks.UZI_HUGE);
                        entries.add(ModBlocks.CYN_PLUSH);
                        entries.add(ModBlocks.SIGMA_BLOCK);
                        entries.add(ModBlocks.THE_DUCKLER);
                        entries.add(ModBlocks.LUMINITE_BLOCK);
                        // ── New in this update ───────────────────────────────
                        entries.add(ModBlocks.SOLVER_BLOCK);
                        entries.add(ModBlocks.COPPER_NINE_BLOCK);
                        entries.add(ModItems.SOLVER_EYE);
                        entries.add(ModBlocks.DOLL_PLUSH);
                    }).build());

    public static void registerItemGroups() {
        Wots.LOGGER.info("Registering Item Groups for " + Wots.MOD_ID);
    }
}
