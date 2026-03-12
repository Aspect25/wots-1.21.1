package net.wots.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.wots.Wots;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> UZI_PLUSHIES = createTag("uzi_plushies");


        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Wots.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> UZI_PLUSHIES = createTag("uzi_plushies");
        public static final TagKey<Item> TRACKED_TRASH_ITEMS = TagKey.of(Registries.ITEM.getKey(), Identifier.of("wots", "tracked_trash_items"));

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Wots.MOD_ID, name));
        }
    }
}
