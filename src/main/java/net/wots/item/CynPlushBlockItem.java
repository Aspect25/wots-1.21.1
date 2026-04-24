package net.wots.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.item.accessory.PlushieSoundAccessory;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class CynPlushBlockItem extends BlockItem implements PlushieSoundAccessory {

    private final ShuffledSoundQueue soundQueue = new ShuffledSoundQueue(CynPlushBlockEntity.SOUND_DURATIONS);

    public CynPlushBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Cyn Plush");
    }

    @Override
    public void playNextPlushieSound(Player player, ItemStack stack) {
        if (player.level().isClientSide()) return;
        SoundEvent sound = soundQueue.tryAdvance(player.level().getGameTime());
        if (sound == null) return;

        // Attach sound to entity so it follows the player
        ServerLevel serverWorld = (ServerLevel) player.level();
        ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), SoundSource.PLAYERS,
                player, 1.0f, 1.0f, serverWorld.getRandom().nextLong());
        serverWorld.getPlayers(p -> p.distanceToSqr(player) <= 64 * 64)
                .forEach(p -> ((ServerPlayer) p).connection.send(packet));
    }
}
