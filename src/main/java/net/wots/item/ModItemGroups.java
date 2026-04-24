package net.wots.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.wots.Wots;
import net.wots.block.ModBlocks;

public class ModItemGroups {
    public static final CreativeModeTab PLUSH_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(Wots.MOD_ID, "plushies"),
            FabricCreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.UZI_PLUSH))
                    .title(Component.translatable("itemgroup.wots.plushies"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(ModBlocks.UZI_PLUSH);
                        entries.accept(ModBlocks.N_PLUSH);
                        entries.accept(ModBlocks.PLUSHIE_SHELF);
                        entries.accept(ModBlocks.TRASH_BLOCK);
                        entries.accept(ModBlocks.UZI_HUGE);
                        entries.accept(ModBlocks.CYN_PLUSH);
                        entries.accept(ModBlocks.SIGMA_BLOCK);
                        entries.accept(ModBlocks.THE_DUCKLER);
                        entries.accept(ModBlocks.LUMINITE_BLOCK);
                        entries.accept(ModBlocks.PLUSHIE_PEDESTAL);
                        entries.accept(ModBlocks.DOLL_PLUSH);
                        entries.accept(ModBlocks.KINGER_PLUSH);
                        entries.accept(ModBlocks.TESSA_PLUSH);
                        entries.accept(ModBlocks.J_PLUSH);
                        entries.accept(ModBlocks.LIZZY_PLUSH);
                        entries.accept(ModBlocks.CAINE_PLUSH);
                        entries.accept(ModBlocks.POMNI_PLUSH);
                        entries.accept(ModBlocks.JAX_PLUSH);
                        entries.accept(ModBlocks.RIBBIT_PLUSH);
                        entries.accept(ModBlocks.CYN_PLUSH_MAID);
                        entries.accept(ModBlocks.STEVE_PLUSH);
                        entries.accept(ModBlocks.RGB_WOOL);
                        entries.accept(ModBlocks.LAND_MINE);
                    }).build());

    public static void registerItemGroups() {
        Wots.LOGGER.info("Registering Item Groups for " + Wots.MOD_ID);
    }
}
