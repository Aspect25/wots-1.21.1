package net.wots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.wots.block.ModBlocks;
import net.wots.client.PlushieSoundKeyHandler;
import net.wots.item.ModItemGroups;
import net.wots.item.ModItems;
import net.wots.network.*;
import net.wots.recipe.CustomStonecuttingRecipe;
import net.wots.screen.CustomStonecutterScreenHandler;
import net.wots.unlock.UnlockSyncHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wots implements ModInitializer {

	public static final String MOD_ID = "wots";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Block CUSTOM_STONECUTTER = ModBlocks.CUSTOM_STONECUTTER;

	public static final RecipeType<CustomStonecuttingRecipe> CUSTOM_STONECUTTING_TYPE =
			new RecipeType<>() {
				@Override public String toString() { return "wots:custom_stonecutting"; }
			};

	public static final RecipeSerializer<CustomStonecuttingRecipe> CUSTOM_STONECUTTING_SERIALIZER =
			new CustomStonecuttingRecipe.Serializer();

	public static ScreenHandlerType<CustomStonecutterScreenHandler> CUSTOM_STONECUTTER_HANDLER;

	// ── Loot table keys ───────────────────────────────────────────────────────
	private static final RegistryKey<LootTable> ANCIENT_CITY_LOOT =
			RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("chests/ancient_city"));
	private static final RegistryKey<LootTable> SIMPLE_DUNGEON_LOOT =
			RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("chests/simple_dungeon"));
	private static final RegistryKey<LootTable> WOODLAND_MANSION_LOOT =
			RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("chests/woodland_mansion"));
	private static final RegistryKey<LootTable> IGLOO_CHEST_LOOT =
			RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("chests/igloo_chest"));

	@Override
	public void onInitialize() {
		net.wots.sound.ModSounds.registerModSounds();
		net.wots.item.ModItems.registerModItems();
		ModBlocks.registerModBlock();
		ModItemGroups.registerItemGroups();

		// ── Packets ───────────────────────────────────────────────────────────
		SetUziVariantPayload.registerServer();
		SetNVariantPayload.registerServer();

		// ── Variant unlock system ─────────────────────────────────────────────
		SyncVariantUnlocksPayload.register();
		VariantChangeParticlePayload.register();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			UnlockSyncHelper.onPlayerJoin(handler.getPlayer());
		});

		// ── Screen handlers ───────────────────────────────────────────────────
		CUSTOM_STONECUTTER_HANDLER = Registry.register(
				Registries.SCREEN_HANDLER,
				Identifier.of("wots", "custom_stonecutter"),
				new ScreenHandlerType<>(
						(syncId, inventory) -> new CustomStonecutterScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
						FeatureFlags.VANILLA_FEATURES
				)
		);

		// ── Recipes ───────────────────────────────────────────────────────────
		Registry.register(Registries.RECIPE_TYPE,
				Identifier.of("wots", "custom_stonecutting"), CUSTOM_STONECUTTING_TYPE);
		Registry.register(Registries.RECIPE_SERIALIZER,
				Identifier.of("wots", "custom_stonecutting"), CUSTOM_STONECUTTING_SERIALIZER);

		// ── Resource packs ────────────────────────────────────────────────────
		ResourceManagerHelper.registerBuiltinResourcePack(
				Identifier.of("wots", "luminite-shaders"),
				FabricLoader.getInstance().getModContainer("wots").orElseThrow(),
				ResourcePackActivationType.ALWAYS_ENABLED
		);
		PayloadTypeRegistry.playC2S().register(
				PlushieSoundPayloads.HatPayload.ID,
				PlushieSoundPayloads.HatPayload.CODEC
		);
		PayloadTypeRegistry.playC2S().register(
				PlushieSoundPayloads.BackPayload.ID,
				PlushieSoundPayloads.BackPayload.CODEC
		);
		PlushieSoundKeyHandler.registerServerReceiver();

		// ── Copper 9 day/night slipperiness (SERVER side) ────────────────────
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
			var overworld = server.getOverworld();
			if (overworld != null) {
				net.wots.block.CopperNineBlock.isNight = overworld.isNight();
			}
		});

		// ── Loot table injections ─────────────────────────────────────────────
		registerLootInjections();
	}

	/**
	 * Injects WOTS items into vanilla loot tables.
	 *
	 * Ancient City  → Solver Eye (5% chance — rare and appropriately scary)
	 * Simple Dungeon → Uzi Plush (8% chance — she'd be happy in a dungeon)
	 * Woodland Mansion → N Plush (10% chance — he fits somewhere cozy and lost)
	 * Igloo Chest → Cyn Plush (12% chance — cold Russian heritage, fits perfectly)
	 *
	 * These are added as separate loot pools so they don't dilute
	 * existing loot weights. Each pool has exactly one roll.
	 */
	private void registerLootInjections() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

			// ── Ancient City: Solver Eye ──────────────────────────────────────
			if (key.equals(ANCIENT_CITY_LOOT)) {
				tableBuilder.pool(
						LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1))
								.with(ItemEntry.builder(ModItems.SOLVER_EYE)
										.apply(SetCountLootFunction.builder(
												ConstantLootNumberProvider.create(1)))
										.conditionally(RandomChanceLootCondition.builder(0.05f)))
								.build()
				);
				LOGGER.debug("[WOTS] Injected Solver Eye into ancient_city loot");
			}

			// ── Simple Dungeon: Uzi Plush ─────────────────────────────────────
			if (key.equals(SIMPLE_DUNGEON_LOOT)) {
				tableBuilder.pool(
						LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1))
								.with(ItemEntry.builder(ModBlocks.UZI_PLUSH.asItem())
										.apply(SetCountLootFunction.builder(
												ConstantLootNumberProvider.create(1)))
										.conditionally(RandomChanceLootCondition.builder(0.08f)))
								.build()
				);
				LOGGER.debug("[WOTS] Injected Uzi Plush into simple_dungeon loot");
			}

			// ── Woodland Mansion: N Plush ─────────────────────────────────────
			if (key.equals(WOODLAND_MANSION_LOOT)) {
				tableBuilder.pool(
						LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1))
								.with(ItemEntry.builder(ModBlocks.N_PLUSH.asItem())
										.apply(SetCountLootFunction.builder(
												ConstantLootNumberProvider.create(1)))
										.conditionally(RandomChanceLootCondition.builder(0.10f)))
								.build()
				);
				LOGGER.debug("[WOTS] Injected N Plush into woodland_mansion loot");
			}

			// ── Igloo Chest: Cyn Plush ────────────────────────────────────────
			if (key.equals(IGLOO_CHEST_LOOT)) {
				tableBuilder.pool(
						LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1))
								.with(ItemEntry.builder(ModBlocks.CYN_PLUSH.asItem())
										.apply(SetCountLootFunction.builder(
												ConstantLootNumberProvider.create(1)))
										.conditionally(RandomChanceLootCondition.builder(0.12f)))
								.build()
				);
				LOGGER.debug("[WOTS] Injected Cyn Plush into igloo_chest loot");
			}
		});
	}
}