package net.wots.mixin;

import net.minecraft.block.Block;
import net.wots.block.CopperNineBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class CopperNineSlipMixin {

    @Inject(method = "getSlipperiness()F", at = @At("HEAD"), cancellable = true)
    private void wots$dynamicSlip(CallbackInfoReturnable<Float> cir) {
        if (!((Block)(Object)this instanceof CopperNineBlock)) return;
        cir.setReturnValue(CopperNineBlock.isNight ? 0.98f : 0.6f);
    }
}