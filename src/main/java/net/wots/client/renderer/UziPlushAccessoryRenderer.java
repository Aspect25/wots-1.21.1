package net.wots.client.renderer;

import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class UziPlushAccessoryRenderer implements SimpleAccessoryRenderer {

    @Override
    public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference,
                                               EntityModel<M> model, MatrixStack matrices) {
        String slotName = reference.slotName();

        if (!(model instanceof BipedEntityModel<?> bipedModel)) return;

        if (slotName.equals("hat")) {
            ModelPart head = bipedModel.head;
            head.rotate(matrices);
            matrices.translate(0, -0.74, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180)); // flip since origin is mid-model
            matrices.scale(0.6f, 0.6f, 0.6f);

        } else if (slotName.equals("back")) {
            ModelPart body = bipedModel.body;
            body.rotate(matrices);
            matrices.translate(0, 0.4, 0.28);

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180)); // flip right side up!
            matrices.scale(0.7f, 0.7f, 0.7f);
        }
    }
}