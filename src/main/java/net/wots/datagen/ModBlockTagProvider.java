package net.wots.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.wots.block.ModBlocks;
import net.wots.block.entity.TrashBlockEntity;
import net.wots.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(ModTags.Blocks.UZI_PLUSHIES)
                .add(ModBlocks.UZI_PLUSH)
                .add(ModBlocks.UZI_HUGE);



        getOrCreateTagBuilder(BlockTags.WOOL)
                .add(ModBlocks.UZI_PLUSH)
                .add(ModBlocks.N_PLUSH)
                .add(ModBlocks.UZI_HUGE)
                .add(ModBlocks.CYN_PLUSH);




    }
}
