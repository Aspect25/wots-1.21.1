package net.wots.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.item.accessory.PlushieSoundAccessory;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class NPlushBlockItem extends BlockItem implements PlushieSoundAccessory {

    private final ShuffledSoundQueue soundQueue = new ShuffledSoundQueue(NPlushBlockEntity.SOUND_DURATIONS);

    public NPlushBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        var beData = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (beData != null) {
            NbtCompound nbt = beData.copyNbt();
            if (nbt.contains("Variant")) {
                try {
                    NPlushVariant variant = NPlushVariant.valueOf(nbt.getString("Variant"));
                    return Text.literal("N Plush (" + variant.displayName + ")");
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return Text.literal("N Plush");
    }

    @Override
    public void playNextPlushieSound(PlayerEntity player) {
        if (player.getWorld().isClient) return;
        SoundEvent sound = soundQueue.tryAdvance(player.getWorld().getTime());
        if (sound == null) return;

        // Attach sound to entity so it follows the player
        ServerWorld serverWorld = (ServerWorld) player.getWorld();
        PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(
                Registries.SOUND_EVENT.getEntry(sound), SoundCategory.PLAYERS,
                player, 1.0f, 1.0f, serverWorld.random.nextLong());
        serverWorld.getPlayers(p -> p.squaredDistanceTo(player) <= 64 * 64)
                .forEach(p -> ((ServerPlayerEntity) p).networkHandler.sendPacket(packet));
    }
}