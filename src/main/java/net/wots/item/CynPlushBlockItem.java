package net.wots.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.item.accessory.PlushieSoundAccessory;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class CynPlushBlockItem extends BlockItem implements PlushieSoundAccessory {

    private final ShuffledSoundQueue soundQueue = new ShuffledSoundQueue(CynPlushBlockEntity.SOUND_DURATIONS);

    public CynPlushBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Cyn Plush");
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