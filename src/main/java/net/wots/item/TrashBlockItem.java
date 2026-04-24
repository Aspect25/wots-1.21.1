package net.wots.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.function.Consumer;

public class TrashBlockItem extends BlockItem {
    public TrashBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.BOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, TooltipFlag type) {
        tooltip.accept(Component.translatable("block.wots.trash_block.tooltip1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("block.wots.trash_block.tooltip2").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("block.wots.trash_block.tooltip3").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        tooltip.accept(Component.translatable("block.wots.trash_block.tooltip4").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
    }
}
