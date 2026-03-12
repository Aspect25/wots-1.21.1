package net.wots;

import net.fabricmc.api.ModInitializer;

import net.wots.block.ModBlocks;
import net.wots.item.ModItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wots.block.CustomStonecutterBlock;
import net.wots.recipe.CustomStonecuttingRecipe;
import net.wots.screen.CustomStonecutterScreenHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;



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
		ModBlocks.registerModBlock();
		ModItemGroups.registerItemGroups();




		Registry.register(Registries.RECIPE_TYPE,
				Identifier.of("wots", "custom_stonecutting"), CUSTOM_STONECUTTING_TYPE);
		Registry.register(Registries.RECIPE_SERIALIZER,
				Identifier.of("wots", "custom_stonecutting"), CUSTOM_STONECUTTING_SERIALIZER);

		CUSTOM_STONECUTTER_HANDLER = Registry.register(
				Registries.SCREEN_HANDLER,
				Identifier.of("wots", "custom_stonecutter"),
				new ScreenHandlerType<>(
						(syncId, inventory) -> new CustomStonecutterScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY),
						FeatureFlags.VANILLA_FEATURES
				)
		);
	}
}