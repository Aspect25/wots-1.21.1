package net.wots.block.plushies.uziplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.wots.block.entity.UziHugeBlockEntity;
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.AbstractPlushieBlock;

/**
 * Huge Uzi plush -- same variants and voice lines as the regular Uzi plush,
 * just rendered with the uzi_plush_huge model.
 */
public class UziHugeBlock extends AbstractPlushieBlock<UziPlushVariant> {

    public UziHugeBlock(Properties properties) {
        super(properties, "wots:uzi_huge", "uzi",
                UziPlushVariant.values(), UziPlushBlockEntity.DEFAULT_SOUNDS);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(UziHugeBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UziHugeBlockEntity(pos, state);
    }
}
