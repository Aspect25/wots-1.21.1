package net.wots.util;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * Shared utility for rotating VoxelShapes around the Y axis.
 * Previously copy-pasted in UziPlushBlock, CynPlushBlock, TheDucklerPlush, and SigmaBlock.
 */
public final class VoxelShapeHelper {

    private VoxelShapeHelper() {}

    /** Rotates a VoxelShape clockwise by 90° × {@code times} around the Y axis. */
    public static VoxelShape rotateShape(VoxelShape shape, int times) {
        VoxelShape[] buffer = { VoxelShapes.empty() };
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX, z1 = minZ, x2 = maxX, z2 = maxZ;
            for (int i = 0; i < times; i++) {
                double newX1 = 1 - z2, newZ1 = x1, newX2 = 1 - z1, newZ2 = x2;
                x1 = newX1; z1 = newZ1; x2 = newX2; z2 = newZ2;
            }
            buffer[0] = VoxelShapes.combine(buffer[0],
                    VoxelShapes.cuboid(x1, minY, z1, x2, maxY, z2),
                    BooleanBiFunction.OR);
        });
        return buffer[0];
    }
}
