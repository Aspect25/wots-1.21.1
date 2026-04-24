package net.wots.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
import net.wots.Wots;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> UZI_PLUSHIES = createTag("uzi_plushies");


        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> UZI_PLUSHIES = createTag("uzi_plushies");
        public static final TagKey<Item> EQUIPPABLE_PLUSHIES = createTag("equippable_plushies");
        public static final TagKey<Item> TRACKED_TRASH_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("wots", "tracked_trash_items"));

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wots.MOD_ID, name));
        }
    }
}
