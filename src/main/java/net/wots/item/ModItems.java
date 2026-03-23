package net.wots.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wots.Wots;

public class ModItems {

    public static final Item METH = registerItem("meth", new Item(new Item.Settings()));

    /**
     * Solver Eye — found only in Ancient City chests (5% chance). Cannot be crafted.
     *
     * Quirk: holding it in your offhand at night makes the screen pulse with
     * a very faint Solver-yellow tint. No other gameplay effect. Just unsettling.
     *
     * Client-side overlay is in SolverEyeOverlay, registered in WotsClient.
     */
    public static final Item SOLVER_EYE = registerItem("solver_eye", new SolverEyeItem(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Wots.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Wots.LOGGER.info("Registering Mod Items for " + Wots.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(SOLVER_EYE);
        });
    }
}
