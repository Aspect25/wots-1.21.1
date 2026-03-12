package net.wots.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wots.Wots;

public class ModItems {

    public static final Item METH = registerItem("meth", new Item(new Item.Settings()));
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Wots.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Wots.LOGGER.info("Registering Mod Items for " + Wots.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(METH);

        });
    }
}