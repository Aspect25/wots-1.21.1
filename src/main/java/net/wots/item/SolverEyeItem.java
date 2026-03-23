package net.wots.item;

import net.minecraft.item.Item;

/**
 * Solver Eye — found rarely in Ancient City chests (5% chance).
 * Cannot be crafted.
 *
 * QUIRK: When held in the offhand at night, the screen pulses with
 * a faint Solver-yellow tint. No gameplay effect. Just deeply unsettling.
 * Some players will throw it away immediately. Others will specifically
 * seek it out for the atmosphere. Polarizing by design.
 *
 * The actual overlay rendering is in SolverEyeOverlay (client-side).
 * This class is intentionally minimal — the item itself is unremarkable.
 * The quirk IS the item.
 */
public class SolverEyeItem extends Item {

    public SolverEyeItem(Settings settings) {
        super(settings);
    }
}
