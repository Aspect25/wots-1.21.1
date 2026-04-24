package net.wots.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.wots.Wots;
import net.wots.block.entity.*;
import net.wots.block.luminite.LuminiteBlock;
import net.wots.block.plushies.cynplush.CynPlushBlock;
import net.wots.block.plushies.dollplush.DollPlushBlock;
import net.wots.block.plushies.nplush.NPlushBlock;
import net.wots.block.plushies.pedestal.PlushiePedestalBlock;
import net.wots.block.plushies.tadc.kinger.KingerPlushBlock;
import net.wots.block.plushies.jplush.JPlushBlock;
import net.wots.block.plushies.tadc.sigma.SigmaBlock;
import net.wots.block.plushies.tessaplush.TessaPlushBlock;
import net.wots.block.plushies.the_duckler.TheDucklerPlush;
import net.wots.block.plushies.caineplush.CainePlushBlock;
import net.wots.block.plushies.caineplush.CainePlushVariant;
import net.wots.block.plushies.cynplushmaid.CynPlushMaidBlock;
import net.wots.block.plushies.cynplushmaid.CynPlushMaidVariant;
import net.wots.block.plushies.lizzyplush.LizzyPlushBlock;
import net.wots.block.plushies.pomniplush.PomniPlushBlock;
import net.wots.block.plushies.pomniplush.PomniPlushVariant;
import net.wots.block.plushies.jaxplush.JaxPlushBlock;
import net.wots.block.plushies.jaxplush.JaxPlushVariant;
import net.wots.block.plushies.ribbitplush.RibbitPlushBlock;
import net.wots.block.plushies.ribbitplush.RibbitPlushVariant;
import net.wots.block.plushies.steveplush.StevePlushBlock;
import net.wots.block.plushies.uziplush.UziHugeBlock;
import net.wots.block.plushies.uziplush.UziPlushBlock;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.block.plushies.dollplush.DollPlushVariant;
import net.wots.block.plushies.tadc.kinger.KingerPlushVariant;
import net.wots.block.plushies.tessaplush.TessaPlushVariant;
import net.wots.block.plushies.jplush.JPlushVariant;
import net.wots.block.plushies.lizzyplush.LizzyPlushVariant;
import net.wots.block.plushies.cynplush.CynPlushVariant;
import net.wots.block.plushies.PlushieVariant;
import net.wots.item.*;
import net.wots.sound.ModSounds;

public class ModBlocks {

    // ── Plushies ──────────────────────────────────────────────────────────────
    public static final Block UZI_PLUSH = registerPlushie("uzi_plush",
            new UziPlushBlock(blockProps("uzi_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Uzi Plush", UziPlushVariant.class, "uzi_plush", "uzi_plush",
            UziPlushBlockEntity.SOUND_DURATIONS, UziPlushBlockEntity.VARIANT_SOUND_MAP);

    public static final BlockEntityType<UziPlushBlockEntity> UZI_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "uzi_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(UziPlushBlockEntity::new,
                            UZI_PLUSH).build()
            );


    public static final Block N_PLUSH = registerPlushie("n_plush",
            new NPlushBlock(blockProps("n_plush")
                    .noOcclusion()
                    .strength(0.3f)
                    .sound(ModSounds.PLUSH_SOUND_GROUP)
                    .lightLevel(state -> 2)),
            "N Plush", NPlushVariant.class, "n_plush", "n_plush", NPlushBlockEntity.SOUND_DURATIONS);
    public static final BlockEntityType<NPlushBlockEntity> N_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "n_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(NPlushBlockEntity::new, N_PLUSH).build()
            );

    public static final UziHugeBlock UZI_HUGE = registerPlushie("uzi_huge",
            new UziHugeBlock(blockProps("uzi_huge").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Huge Uzi", UziPlushVariant.class, "uzi_plush_huge", "uzi_plush", UziPlushBlockEntity.DEFAULT_SOUNDS,
            UziPlushBlockEntity.VARIANT_SOUND_MAP);
    public static final BlockEntityType<UziHugeBlockEntity> UZI_HUGE_BLOCK_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath("wots", "uzi_huge_block_entity"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(UziHugeBlockEntity::new, UZI_HUGE).build());


    public static final CynPlushBlock CYN_PLUSH = registerPlushie("cyn_plush",
            new CynPlushBlock(blockProps("cyn_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Cyn Plush", CynPlushVariant.class, "cyn_plush", "cyn_plush", CynPlushBlockEntity.SOUND_DURATIONS);
    public static final BlockEntityType<CynPlushBlockEntity> CYN_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "cyn_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(CynPlushBlockEntity::new, CYN_PLUSH).build());
    public static final DollPlushBlock DOLL_PLUSH = registerPlushie("doll_plush",
            new DollPlushBlock(blockProps("doll_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Doll Plush", DollPlushVariant.class, "doll_plush", "doll_plush", DollPlushBlockEntity.SOUND_DURATIONS);
    public static final BlockEntityType<DollPlushBlockEntity> DOLL_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "doll_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(DollPlushBlockEntity::new, DOLL_PLUSH).build());

    // ── Kinger Plush (TADC) ───────────────────────────────────────────────────
    public static final KingerPlushBlock KINGER_PLUSH = registerPlushie("kinger_plush",
            new KingerPlushBlock(blockProps("kinger_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Kinger Plush", KingerPlushVariant.class, "kinger_plush", "j_plush", null);
    public static final BlockEntityType<KingerPlushBlockEntity> KINGER_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "kinger_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(KingerPlushBlockEntity::new, KINGER_PLUSH).build());

    // ── Tessa Plush (Murder Drones) ───────────────────────────────────────────
    public static final TessaPlushBlock TESSA_PLUSH = registerPlushie("tessa_plush",
            new TessaPlushBlock(blockProps("tessa_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Tessa Plush", TessaPlushVariant.class, "tessa_plush", "j_plush", null);
    public static final BlockEntityType<TessaPlushBlockEntity> TESSA_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "tessa_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(TessaPlushBlockEntity::new, TESSA_PLUSH).build());

    // ── J Plush (Murder Drones) ───────────────────────────────────────────────
    public static final JPlushBlock J_PLUSH = registerPlushie("j_plush",
            new JPlushBlock(blockProps("j_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "J Plush", JPlushVariant.class, "j_plush", "j_plush", null);
    public static final BlockEntityType<JPlushBlockEntity> J_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "j_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(JPlushBlockEntity::new, J_PLUSH).build());


    // ── Lizzy Plush ────────────────────────────────────────────────────────
    public static final LizzyPlushBlock LIZZY_PLUSH = registerPlushie("lizzy_plush",
            new LizzyPlushBlock(blockProps("lizzy_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Lizzy Plush", LizzyPlushVariant.class, "lizzy_plush", "lizzy_plush", null);
    public static final BlockEntityType<LizzyPlushBlockEntity> LIZZY_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "lizzy_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(LizzyPlushBlockEntity::new, LIZZY_PLUSH).build());

    // ── Caine Plush (TADC) ─────────────────────────────────────────────────────
    public static final CainePlushBlock CAINE_PLUSH = registerPlushie("caine_plush",
            new CainePlushBlock(blockProps("caine_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Caine Plush", CainePlushVariant.class, "caine", "j_plush", null);
    public static final BlockEntityType<CainePlushBlockEntity> CAINE_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "caine_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(CainePlushBlockEntity::new, CAINE_PLUSH).build());

    // ── Ribbit Plush ─────────────────────────────────────────────────────────
    public static final RibbitPlushBlock RIBBIT_PLUSH = registerPlushie("ribbit_plush",
            new RibbitPlushBlock(blockProps("ribbit_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Ribbit Plush", RibbitPlushVariant.class, "ribbit", "j_plush", null);
    public static final BlockEntityType<RibbitPlushBlockEntity> RIBBIT_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "ribbit_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(RibbitPlushBlockEntity::new, RIBBIT_PLUSH).build());

    // ── Pomni Plush (TADC) ──────────────────────────────────────────────────
    public static final PomniPlushBlock POMNI_PLUSH = registerPlushie("pomni_plush",
            new PomniPlushBlock(blockProps("pomni_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Pomni Plush", PomniPlushVariant.class, "pomni_plush", "j_plush", null);
    public static final BlockEntityType<PomniPlushBlockEntity> POMNI_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "pomni_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(PomniPlushBlockEntity::new, POMNI_PLUSH).build());

    // ── Jax Plush (TADC) ────────────────────────────────────────────────────
    public static final JaxPlushBlock JAX_PLUSH = registerPlushie("jax_plush",
            new JaxPlushBlock(blockProps("jax_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Jax Plush", JaxPlushVariant.class, "jax_plush", "j_plush", null);
    public static final BlockEntityType<JaxPlushBlockEntity> JAX_PLUSH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "jax_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(JaxPlushBlockEntity::new, JAX_PLUSH).build());

    // ── Cyn Plush Maid ───────────────────────────────────────────────────────
    public static final CynPlushMaidBlock CYN_PLUSH_MAID = registerPlushie("cyn_plush_maid",
            new CynPlushMaidBlock(blockProps("cyn_plush_maid").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP)),
            "Cyn Plush Maid", CynPlushMaidVariant.class, "cyn_plush_maid", "cyn_plush", CynPlushMaidBlockEntity.SOUND_DURATIONS);
    public static final BlockEntityType<CynPlushMaidBlockEntity> CYN_PLUSH_MAID_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "cyn_plush_maid"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(CynPlushMaidBlockEntity::new, CYN_PLUSH_MAID).build());

    // ── Steve Plush (player skin plush) ─────────────────────────────────────
    private static final StevePlushBlock STEVE_PLUSH_INSTANCE = new StevePlushBlock(
            blockProps("steve_plush").noOcclusion().strength(0.1f).sound(ModSounds.PLUSH_SOUND_GROUP));
    public static final StevePlushBlock STEVE_PLUSH = registerBlock("steve_plush",
            STEVE_PLUSH_INSTANCE,
            new net.wots.item.StevePlushBlockItem(STEVE_PLUSH_INSTANCE, itemProps("steve_plush")));
    public static final BlockEntityType<StevePlushBlockEntity> STEVE_PLUSH_BLOCK_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "steve_plush"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(StevePlushBlockEntity::new, STEVE_PLUSH).build());

    private static final SigmaBlock SIGMA_BLOCK_INSTANCE = new SigmaBlock(blockProps("sigma_block").noOcclusion().sound(SoundType.WOOL));
    public static final SigmaBlock SIGMA_BLOCK = registerBlock("sigma_block",
            SIGMA_BLOCK_INSTANCE,
            new net.wots.item.SigmaBlockItem(SIGMA_BLOCK_INSTANCE, itemProps("sigma_block"))
    );
    public static final TheDucklerPlush THE_DUCKLER = registerBlock("the_duckler",
            new TheDucklerPlush(blockProps("the_duckler").noOcclusion().strength(0.5f).sound(ModSounds.DUCKLER_SOUND_GROUP)));


    public static final BlockEntityType<SigmaBlockEntity> SIGMA_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Wots.MOD_ID, "sigma_entity"),
            net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(SigmaBlockEntity::new, ModBlocks.SIGMA_BLOCK).build()
    );
    public static final Block LUMINITE_BLOCK = registerBlock("luminite_block",
            new LuminiteBlock(
                    blockProps("luminite_block")
                            .strength(1.5f, 6.0f)
                            .lightLevel(state -> {
                                if (!state.getValue(LuminiteBlock.LIT)) return 0;
                                return Math.round(state.getValue(LuminiteBlock.POWER) * 0.6f);
                            })
                            .sound(SoundType.AMETHYST)
                            .emissiveRendering((state, world, pos) -> state.getValue(LuminiteBlock.LIT))
                            .noOcclusion()
            )
    );

    public static final Block LAND_MINE = registerBlock("land_mine",
            new LandMineBlock(blockProps("land_mine").noOcclusion().strength(0.5f).sound(SoundType.STONE)));

    // ── RGB Wool ─────────────────────────────────────────────────────────────
    public static final RgbWoolBlock RGB_WOOL = registerBlock("rgb_wool",
            new RgbWoolBlock(blockProps("rgb_wool")
                    .strength(0.8f)
                    .sound(SoundType.WOOL)
                    .ignitedByLava()));
    public static final BlockEntityType<RgbWoolBlockEntity> RGB_WOOL_BLOCK_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "rgb_wool"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(RgbWoolBlockEntity::new, RGB_WOOL).build());

    // ── Machines ─────────────────────────────────────────────────────────────
    private static final TrashBlock TRASH_BLOCK_INSTANCE = new TrashBlock(blockProps("trash_block").noOcclusion().sound(SoundType.STONE));
    public static final Block TRASH_BLOCK = registerBlock("trash_block", TRASH_BLOCK_INSTANCE,
            new TrashBlockItem(TRASH_BLOCK_INSTANCE, itemProps("trash_block")));

    public static final BlockEntityType<TrashBlockEntity> TRASH_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "trash_block"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(TrashBlockEntity::new, TRASH_BLOCK).build()
            );

    // ── Plushie Shelf ─────────────────────────────────────────────────────────
    public static final PlushieShelfBlock PLUSHIE_SHELF = registerBlock("plushie_shelf",
            new PlushieShelfBlock(blockProps("plushie_shelf").noOcclusion().sound(SoundType.WOOD)));

    public static final BlockEntityType<PlushieShelfBlockEntity> PLUSHIE_SHELF_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "plushie_shelf"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(PlushieShelfBlockEntity::new, PLUSHIE_SHELF).build()
            );

    // ── Plushie Pedestal ──────────────────────────────────────────────────────
    public static final PlushiePedestalBlock PLUSHIE_PEDESTAL = registerBlock("plushie_pedestal",
            new PlushiePedestalBlock(blockProps("plushie_pedestal")
                    .noOcclusion()
                    .strength(1.5f)
                    .sound(SoundType.STONE)));

    public static final BlockEntityType<PlushiePedestalBlockEntity> PLUSHIE_PEDESTAL_BLOCK_ENTITY =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Wots.MOD_ID, "plushie_pedestal"),
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(PlushiePedestalBlockEntity::new, PLUSHIE_PEDESTAL).build()
            );

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Create BlockBehaviour.Properties pre-seeded with the block's registry key. */
    public static BlockBehaviour.Properties blockProps(String name) {
        return BlockBehaviour.Properties.of().setId(
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name)));
    }

    /** Create Item.Properties pre-seeded with the item's registry key. */
    private static Item.Properties itemProps(String name) {
        return new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name)));
    }

    /** Register a block with a default BlockItem. */
    private static <T extends Block> T registerBlock(String name, T block) {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name),
                new BlockItem(block, itemProps(name)));
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name), block);
    }

    /** Register a block with a custom BlockItem. */
    private static <T extends Block> T registerBlock(String name, T block, BlockItem blockItem) {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name), blockItem);
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name), block);
    }

    /**
     * Register a plushie block with a {@link VariantPlushBlockItem}.
     * Geo and anim names default to the block name.
     */
    private static <V extends Enum<V> & PlushieVariant, T extends Block> T registerPlushie(
            String name, T block, String displayName, Class<V> variantClass,
            String geoName, String animName,
            java.util.Map<SoundEvent, Integer> soundDurations) {
        return registerPlushie(name, block, displayName, variantClass, geoName, animName, soundDurations, null);
    }

    /**
     * Register a plushie with per-variant accessory sounds.
     */
    private static <V extends Enum<V> & PlushieVariant, T extends Block> T registerPlushie(
            String name, T block, String displayName, Class<V> variantClass,
            String geoName, String animName,
            java.util.Map<SoundEvent, Integer> soundDurations,
            java.util.Map<V, java.util.Map<SoundEvent, Integer>> variantSoundMap) {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name),
                new VariantPlushBlockItem<>(block, itemProps(name), displayName, variantClass, geoName, animName, soundDurations, variantSoundMap));
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name), block);
    }

    public static void registerModBlock() {
        Wots.LOGGER.info("Registering Mod Blocks for " + Wots.MOD_ID);
        // TODO [26.1] FuelRegistry.INSTANCE.add() removed. Use FuelValueEvents or data-driven fuel values.
        // FuelRegistry: UZI_PLUSH 100, N_PLUSH 100, CYN_PLUSH 100
    }
}
