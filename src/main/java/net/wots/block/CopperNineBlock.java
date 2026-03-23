package net.wots.block;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class CopperNineBlock extends Block {

    // Updated every client tick by WotsClient.
    public static boolean isNight = false;

    public CopperNineBlock(Settings settings) {
        super(settings);
    }
}