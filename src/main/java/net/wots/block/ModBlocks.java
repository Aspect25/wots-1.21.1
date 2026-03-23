package net.wots.block;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
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
import net.wots.block.plushies.dollplush.DollPlushBlock;
import net.wots.block.plushies.nplush.NPlushBlock;
import net.wots.block.plushies.tadc.sigma.SigmaBlock;
import net.wots.block.plushies.the_duckler.TheDucklerPlush;
import net.wots.block.plushies.uziplush.UziHugeBlock;
import net.wots.block.plushies.uziplush.UziHugePhantomBlock;
import net.wots.block.plushies.uziplush.UziPlushBlock;
import net.wots.item.*;
import net.wots.sound.ModSounds;

public class ModBlocks {

    // ── Plushies ──────────────────────────────────────────────────────────────
    public static final Block UZI_PLUSH = registerBlock("uzi_plush",
            new UziPlushBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.1f).sounds(ModSounds.PLUSH_SOUND_GROUP)));

    public static final BlockEntityType<UziPlushBlockEntity> UZI_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "uzi_plush"),
                    BlockEntityType.Builder.create(UziPlushBlockEntity::new,
                            UZI_PLUSH).build()
            );


    public static final Block N_PLUSH = registerBlock("n_plush",
            new NPlushBlock(AbstractBlock.Settings.create()
                    .nonOpaque()
                    .strength(0.3f)
                    .sounds(ModSounds.PLUSH_SOUND_GROUP)
                    // Subtle always-on glow. Level 2 = barely visible to the naked eye,
                    // but shader users and players in pitch dark will spot it. This is
                    // the "attracts attention in multiplayer" mechanic — passive, not OP.
                    .luminance(state -> 2)));
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
 public static final DollPlushBlock DOLL_PLUSH = registerBlock("doll_plush",
            new DollPlushBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.1f).sounds(ModSounds.PLUSH_SOUND_GROUP)));
    public static final BlockEntityType<DollPlushBlockEntity> DOLL_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Wots.MOD_ID, "doll_plush"),
                    BlockEntityType.Builder.create(DollPlushBlockEntity::new, DOLL_PLUSH).build());


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
                                return Math.round(state.get(LuminiteBlock.POWER) * 0.6f);
                            })
                            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                            .emissiveLighting((state, world, pos) -> state.get(LuminiteBlock.LIT))
                            .nonOpaque()
            )
    );

    public static final Block LAND_MINE = registerBlock("land_mine",
            new LandMineBlock(AbstractBlock.Settings.create().nonOpaque().strength(0.5f).sounds(BlockSoundGroup.STONE)));

    // ── New WOTS decorative blocks ─────────────────────────────────────────────

    /**
     * Solver Block — crafted with Soul Sand + Nether Quartz.
     * Light level 7: good ambient light, but mobs can still spawn nearby.
     * The soul-torch of this mod — pretty, but commit to it at your own risk.
     */
    public static final Block SOLVER_BLOCK = registerBlock("solver_block",
            new SolverBlock(AbstractBlock.Settings.create()
                    .strength(1.5f, 4.0f)
                    .sounds(BlockSoundGroup.NETHER_BRICKS)
                    .luminance(state -> 7)
                    .emissiveLighting((state, world, pos) -> true)));

    /**
     * Copper 9 Block — crafted with Packed Ice + Copper Block.
     * Normal to walk on during the day. At night it becomes slippery,
     * amplifying movement momentum. Mirrors the Copper 9 toxic night storms.
     * Strength = fragile (packed ice equivalent).
     */
    public static final Block COPPER_NINE_BLOCK = registerBlock("copper_nine_block",
            new CopperNineBlock(AbstractBlock.Settings.create()
                    .strength(0.5f)
                    .sounds(BlockSoundGroup.GLASS)));

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
        } else if (block instanceof DollPlushBlock) {
            item = new DollPlushBlockItem(block, new Item.Settings());
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