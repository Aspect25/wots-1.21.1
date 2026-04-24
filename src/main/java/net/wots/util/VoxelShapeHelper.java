package net.wots.util;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

/**
 * Shared utility for rotating Shapes around the Y axis.
 * Previously copy-pasted in UziPlushBlock, CynPlushBlock, TheDucklerPlush, and SigmaBlock.
 */
public final class VoxelShapeHelper {

    private VoxelShapeHelper() {}

    /** Rotates a VoxelShape clockwise by 90° × {@code times} around the Y axis. */
    public static VoxelShape rotateShape(VoxelShape shape, int times) {
        VoxelShape[] buffer = { Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX, z1 = minZ, x2 = maxX, z2 = maxZ;
            for (int i = 0; i < times; i++) {
                double newX1 = 1 - z2, newZ1 = x1, newX2 = 1 - z1, newZ2 = x2;
                x1 = newX1; z1 = newZ1; x2 = newX2; z2 = newZ2;
            }
            buffer[0] = Shapes.join(buffer[0],
                    Shapes.box(x1, minY, z1, x2, maxY, z2),
                    BooleanOp.OR);
        });
        return buffer[0];
    }
}
