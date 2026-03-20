package net.wots.block;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.wots.Wots;
import net.wots.block.entity.*;
import net.wots.block.luminite.LuminiteBlock;
import net.wots.block.plushies.cynplush.CynPlushBlock;
import net.wots.block.plushies.nplush.NPlushBlock;
import net.wots.block.plushies.tadc.sigma.SigmaBlock;
import net.wots.block.plushies.the_duckler.TheDucklerPlush;
import net.wots.block.plushies.uziplush.UziHugeBlock;
import net.wots.block.plushies.uziplush.UziHugePhantomBlock;
import net.wots.block.plushies.uziplush.UziPlushBlock;
import net.wots.item.CynPlushBlockItem;
import net.wots.item.NPlushBlockItem;
import net.wots.item.TrashBlockItem;
import net.wots.item.UziPlushBlockItem;
import net.wots.sound.ModSounds;

public class ModBlocks {

    // ── Plushies ──────────────────────────────────────────────────────────────
    public static final Block UZI_PLUSH = registerBlock("uzi_plush",
            new UziPlushBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.1f).sounds(ModSounds.PLUSH_SOUND_GROUP)));

    // All variants listed in the block entity registration
    public static final BlockEntityType<UziPlushBlockEntity> UZI_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "uzi_plush"),
                    BlockEntityType.Builder.create(UziPlushBlockEntity::new,
                            UZI_PLUSH).build()
            );


    public static final Block N_PLUSH = registerBlock("n_plush",
            new NPlushBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.1f).sounds(ModSounds.PLUSH_SOUND_GROUP)));
    public static final BlockEntityType<NPlushBlockEntity> N_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "n_plush"),
                    BlockEntityType.Builder.create(NPlushBlockEntity::new, N_PLUSH).build()
            );

    public static final UziHugeBlock UZI_HUGE = registerBlock("uzi_huge",
            new UziHugeBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.1f).sounds(ModSounds.PLUSH_SOUND_GROUP)));
    public static final BlockEntityType<UziHugeBlockEntity> UZI_HUGE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of("wots", "uzi_huge_block_entity"),
                    BlockEntityType.Builder.create(UziHugeBlockEntity::new, UZI_HUGE).build());

    public static final UziHugePhantomBlock UZI_HUGE_PHANTOM = registerBlock("uzi_huge_phantom",
            new UziHugePhantomBlock(AbstractBlock.Settings.create()
                    .nonOpaque()
                    .noBlockBreakParticles()
                    .strength(0.1f)
                    .sounds(ModSounds.PLUSH_SOUND_GROUP)));

    public static final CynPlushBlock CYN_PLUSH = registerBlock("cyn_plush",
            new CynPlushBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.1f).sounds(ModSounds.PLUSH_SOUND_GROUP)));
    public static final BlockEntityType<CynPlushBlockEntity> CYN_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "cyn_plush"),
                    BlockEntityType.Builder.create(CynPlushBlockEntity::new, CYN_PLUSH).build());


    public static final SigmaBlock SIGMA_BLOCK = registerBlock("sigma_block",
            new SigmaBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.WOOL))
    );
    public static final TheDucklerPlush THE_DUCKLER = registerBlock("the_duckler",
            new TheDucklerPlush(AbstractBlock.Settings.create().nonOpaque().strength(0.5f).sounds(ModSounds.DUCKLER_SOUND_GROUP)));


    public static final BlockEntityType<SigmaBlockEntity> SIGMA_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Wots.MOD_ID, "sigma_entity"),
            BlockEntityType.Builder.create(SigmaBlockEntity::new, ModBlocks.SIGMA_BLOCK).build(null)
    );
    public static final Block LUMINITE_BLOCK = registerBlock("luminite_block",
            new LuminiteBlock(
                    AbstractBlock.Settings.create()
                            .strength(1.5f, 6.0f)
                            .luminance(state -> {
                                if (!state.get(LuminiteBlock.LIT)) return 0;
                                // Soft ambient glow — max level 9 instead of blinding 15
                                return Math.round(state.get(LuminiteBlock.POWER) * 0.6f);
                            })
                            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                            // Emissive: block face renders at full brightness regardless of light level
                            // Also tells Iris shaders to cast colored light from the texture
                            .emissiveLighting((state, world, pos) -> state.get(LuminiteBlock.LIT))
                            .nonOpaque()
            )
    );

    public static final Block LAND_MINE = registerBlock("land_mine",
            new LandMineBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.5f).sounds(BlockSoundGroup.STONE)));






    // ── Machines ──────────────────────────────────────────────────────────────
    public static final Block CUSTOM_STONECUTTER = registerBlock("custom_stonecutter",
            new CustomStonecutterBlock(AbstractBlock.Settings.create().nonOpaque().sounds(ModSounds.TV_SOUND_GROUP)));

    public static final Block TRASH_BLOCK = registerBlock("trash_block",
            new TrashBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.STONE)));

    public static final BlockEntityType<TrashBlockEntity> TRASH_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "trash_block"),
                    BlockEntityType.Builder
                            .create(TrashBlockEntity::new, TRASH_BLOCK)
                            .build()
            );

    // ── Plushie Shelf ─────────────────────────────────────────────────────────
    // Block must be declared before the block entity type!
    public static final PlushieShelfBlock PLUSHIE_SHELF = registerBlock("plushie_shelf",
            new PlushieShelfBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.WOOD)));

    public static final BlockEntityType<PlushieShelfBlockEntity> PLUSHIE_SHELF_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "plushie_shelf"),
                    BlockEntityType.Builder
                            .create(PlushieShelfBlockEntity::new, PLUSHIE_SHELF)
                            .build()
            );

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static <T extends Block> T registerBlock(String name, T block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Wots.MOD_ID, name), block);
    }
    private static void registerBlockItem(String name, Block block) {
        BlockItem item;
        if (block instanceof TrashBlock) {
            item = new TrashBlockItem(block, new Item.Settings());
        } else if (block instanceof UziPlushBlock) {
            item = new UziPlushBlockItem(block, new Item.Settings());
        } else if (block instanceof NPlushBlock) {
            item = new NPlushBlockItem(block, new Item.Settings());
        } else if (block instanceof CynPlushBlock) {
            item = new CynPlushBlockItem(block, new Item.Settings());
        } else {
            item = new BlockItem(block, new Item.Settings());
        }
        Registry.register(Registries.ITEM, Identifier.of(Wots.MOD_ID, name), item);
    }
    private static <T extends Block> T registerBlock(String name, T block, BlockItem blockItem) {
        Registry.register(Registries.ITEM, Identifier.of(Wots.MOD_ID, name), blockItem);
        return Registry.register(Registries.BLOCK, Identifier.of(Wots.MOD_ID, name), block);
    }

    public static void registerModBlock() {
        Wots.LOGGER.info("Registering Mod Blocks for " + Wots.MOD_ID);
        FuelRegistry.INSTANCE.add(UZI_PLUSH.asItem(), 100);
        FuelRegistry.INSTANCE.add(N_PLUSH.asItem(),   100);
        FuelRegistry.INSTANCE.add(CYN_PLUSH.asItem(), 100);

    }
}