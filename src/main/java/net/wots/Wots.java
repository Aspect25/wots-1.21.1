package net.wots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.wots.block.ModBlocks;
import net.wots.client.PlushieSoundKeyHandler;
import net.wots.item.ModItemGroups;
import net.wots.network.*;
import net.wots.screen.CustomStonecutterScreenHandler;
import net.wots.recipe.CustomStonecuttingRecipe;
import net.wots.unlock.UnlockSyncHelper;
import net.minecraft.block.Block;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
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

		// ── Events ────────────────────────────────────────────────────────────


	}
}
