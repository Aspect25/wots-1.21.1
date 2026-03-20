package net.wots.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.item.accessory.PlushieSoundAccessory;

import java.util.*;

public class NPlushBlockItem extends BlockItem implements PlushieSoundAccessory {

    private static final List<SoundEvent> sounds =
            new ArrayList<>(NPlushBlockEntity.SOUND_DURATIONS.keySet());
    private static int soundIndex = 0;
    private static SoundEvent lastPlayed = null;
    private static long cooldownEnd = 0L;

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

        long now = player.getWorld().getTime();
        if (now < cooldownEnd) return;

        if (soundIndex == 0) {
            Collections.shuffle(sounds);
            if (sounds.size() > 1 && sounds.get(0).equals(lastPlayed)) {
                Collections.swap(sounds, 0, 1);
            }
        }

        SoundEvent sound = sounds.get(soundIndex);
        lastPlayed = sound;
        soundIndex = (soundIndex + 1) % sounds.size();
        cooldownEnd = now + NPlushBlockEntity.SOUND_DURATIONS.get(sound);

        player.getWorld().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                sound,
                SoundCategory.PLAYERS,
                1.0f, 1.0f
        );
    }
}