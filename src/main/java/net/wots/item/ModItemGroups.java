package net.wots.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wots.Wots;
import net.wots.block.CustomStonecutterBlock;
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
                        entries.add(ModBlocks.UZI_PLUSH_SADGE);
                        entries.add(ModBlocks.UZI_PLUSH_SCAREDAF);
                        entries.add(ModBlocks.UZI_PLUSH_SPOOKED);
                        entries.add(ModBlocks.UZI_PLUSH_TRAUMATIZED);
                        entries.add(ModBlocks.UZI_PLUSH_UNAMUSED);
                        entries.add(ModBlocks.UZI_PLUSH_ANGY);
                        entries.add(ModBlocks.UZI_PLUSH_ANGYAF);
                        entries.add(ModBlocks.UZI_PLUSH_DRUNK);
                        entries.add(ModBlocks.UZI_PLUSH_HAPPY);
                        entries.add(ModBlocks.UZI_PLUSH_OHNO);
                        entries.add(ModBlocks.UZI_PLUSH_WORRIED);
                        entries.add(ModBlocks.UZI_PLUSH_WORRIEDAF);
                        entries.add(ModBlocks.PLUSHIE_SHELF);
                        entries.add(ModBlocks.TRASH_BLOCK);
                        entries.add(ModBlocks.UZI_HUGE);
                        entries.add(ModBlocks.CYN_PLUSH);
                        entries.add(ModBlocks.SIGMA_BLOCK);
                        entries.add(ModBlocks.THE_DUCKLER);
                    }).build());



    public static void registerItemGroups() {
        Wots.LOGGER.info("Registering Item Groups for " + Wots.MOD_ID);
    }
}
