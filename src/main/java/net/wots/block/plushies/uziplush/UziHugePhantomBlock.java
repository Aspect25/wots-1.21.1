package net.wots.block.plushies.uziplush;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.HashSet;
import java.util.Set;

public class UziHugePhantomBlock extends Block {

    private static final Set<BlockPos> BREAKING = new HashSet<>();

    public UziHugePhantomBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        System.out.println("[UziPhantom] onBreak called at " + pos + " isClient=" + world.isClient);
        if (!world.isClient) {
            BlockPos origin = findOrigin(world, pos);
            System.out.println("[UziPhantom] origin found: " + origin);

            if (origin != null && !BREAKING.contains(origin)) {
                BREAKING.add(origin);
                try {
                    if (!player.isCreative()) {
                        Block.dropStacks(world.getBlockState(origin), world, origin,
                                world.getBlockEntity(origin), player, player.getMainHandStack());
                    }

                    // Remove all phantoms
                    for (BlockPos offset : UziHugeBlock.OFFSETS) {
                        BlockPos phantomPos = origin.add(offset);
                        if (world.getBlockState(phantomPos).getBlock() instanceof UziHugePhantomBlock) {
                            world.removeBlock(phantomPos, false);
                        }
                    }

                    // Remove origin
                    world.removeBlock(origin, false);

                } finally {
                    BREAKING.remove(origin);
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    // Delegates to UziHugeBlock.playFromPos, passing the clicked phantom pos
    // so SPP raycasts to exactly where the player clicked — no muffling
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        BlockPos origin = findOrigin(world, pos);
        if (origin != null) {
            BlockState originState = world.getBlockState(origin);
            if (originState.getBlock() instanceof UziHugeBlock uziHuge) {
                uziHuge.playFromPos(world, origin, pos, player);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private BlockPos findOrigin(World world, BlockPos phantomPos) {
        for (int x = -2; x <= 2; x++)
            for (int y = -2; y <= 2; y++)
                for (int z = -2; z <= 2; z++) {
                    BlockPos candidate = phantomPos.add(x, y, z);
                    if (world.getBlockState(candidate).getBlock() instanceof UziHugeBlock)
                        return candidate;
                }
        return null;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }
}