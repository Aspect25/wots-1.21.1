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
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.item.accessory.PlushieSoundAccessory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UziPlushBlockItem extends BlockItem implements PlushieSoundAccessory {

    // ── Sound state ───────────────────────────────────────────────────────────
    private final List<SoundEvent> sounds = new ArrayList<>(UziPlushBlockEntity.SOUND_DURATIONS.keySet());
    private int        soundIndex  = 0;
    private SoundEvent lastPlayed  = null;
    private long       cooldownEnd = 0L;

    public UziPlushBlockItem(Block block, Settings settings) {
        super(block, settings);
        Collections.shuffle(sounds);
    }

    // ── PlushieSoundAccessory ─────────────────────────────────────────────────

    @Override
    public void playNextPlushieSound(PlayerEntity player) {
        if (player.getWorld().isClient) return;

        long now = player.getWorld().getTime();
        if (now < cooldownEnd) return;

        if (soundIndex == 0) {
            Collections.shuffle(sounds);
            if (sounds.size() > 1 && sounds.get(0).equals(lastPlayed)) {
                Collections.swap(sounds, 0, 1);
            }
        }

        SoundEvent sound = sounds.get(soundIndex);
        lastPlayed  = sound;
        soundIndex  = (soundIndex + 1) % sounds.size();
        cooldownEnd = now + UziPlushBlockEntity.SOUND_DURATIONS.get(sound);

        // Build a packet that attaches the sound to the entity so it follows
        // the player as they move, instead of playing from a fixed block pos.
        ServerWorld serverWorld = (ServerWorld) player.getWorld();
        PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(
                Registries.SOUND_EVENT.getEntry(sound),
                SoundCategory.PLAYERS,
                player,
                1.0f,                        // volume
                1.0f,                        // pitch
                serverWorld.random.nextLong() // seed
        );

        // Send to every player within 64 blocks (including the wearer)
        serverWorld.getPlayers(p ->
                p.squaredDistanceTo(player) <= 64 * 64
        ).forEach(p -> ((ServerPlayerEntity) p).networkHandler.sendPacket(packet));
    }

    // ── Display name ──────────────────────────────────────────────────────────

    @Override
    public Text getName(ItemStack stack) {
        var beData = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (beData != null) {
            NbtCompound nbt = beData.copyNbt();
            if (nbt.contains("Variant")) {
                try {
                    UziPlushVariant variant = UziPlushVariant.valueOf(nbt.getString("Variant"));
                    return Text.literal("Uzi Plush (" + variant.displayName + ")");
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return Text.literal("Uzi Plush");
    }
}