package net.wots.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.wots.Wots;
import net.wots.block.ModBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.client.TextureMap.pattern;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    @Override
    public void generate(RecipeExporter exporter) {
        List<ItemConvertible> UZI_PLUSHIES =List.of(ModBlocks.UZI_PLUSH);


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.UZI_PLUSH, 1)
                .pattern("   ")
                .pattern(" I ")
                .pattern(" P ")
                .input('P', Items.PURPLE_WOOL)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.PURPLE_WOOL), conditionsFromItem(Items.PURPLE_WOOL))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"uzi_plush_recipe"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.N_PLUSH, 1)
                .pattern("   ")
                .pattern(" I ")
                .pattern(" P ")
                .input('P', Items.YELLOW_WOOL)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.YELLOW_WOOL), conditionsFromItem(Items.YELLOW_WOOL))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"n_plush_recipe"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CYN_PLUSH, 1)
                .pattern("FFF")
                .pattern("FIF")
                .pattern(" B ")
                .input('B', Items.BROWN_WOOL)
                .input('I', Items.IRON_INGOT)
                .input('F',Items.ROTTEN_FLESH)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.BROWN_WOOL), conditionsFromItem(Items.BROWN_WOOL))
                .criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"cyn_recipe"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.UZI_HUGE, 1)
                .pattern("UUU")
                .pattern("UUU")
                .pattern("UUU")
                .input('U', ModBlocks.UZI_PLUSH)
                .criterion(hasItem(ModBlocks.UZI_PLUSH), conditionsFromItem(ModBlocks.UZI_PLUSH))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"uzi_huge"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PLUSHIE_SHELF)
                .pattern("XXX")
                .input('X', Blocks.COBBLED_DEEPSLATE_SLAB)
                .criterion(hasItem(Blocks.COBBLED_DEEPSLATE_SLAB), conditionsFromItem(Blocks.COBBLED_DEEPSLATE_SLAB))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"plushie_shelf"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CUSTOM_STONECUTTER, 1)
                .pattern("WWW")
                .pattern("WGW")
                .pattern("WRW")
                .input('W', ItemTags.PLANKS)
                .input('G', Blocks.GLASS_PANE)
                .input('R', Items.REDSTONE)
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"tv"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TRASH_BLOCK, 1)
                .pattern("ITI")
                .pattern("I I")
                .pattern("III")
                .input('I', Items.IRON_INGOT)
                .input('T', Items.IRON_TRAPDOOR)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Identifier.of(Wots.MOD_ID,"trash"));

    }
}