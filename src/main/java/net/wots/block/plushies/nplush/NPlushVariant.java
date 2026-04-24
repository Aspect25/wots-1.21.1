package net.wots.block.plushies.nplush;

import net.wots.block.plushies.PlushieVariant;

public enum NPlushVariant implements PlushieVariant {
    N_PLUSH          ("Default",       0x9B8BB4),
    N_PLUSH_ANGY     ("Angy",          0xC94C4C),
    N_PLUSH_ANGYAF   ("Angy AF",       0xFF2222),
    N_PLUSH_DRUNK    ("Drunk",         0xC47AB0),
    N_PLUSH_HAPPY    ("Happy",         0xF0C030),
    N_PLUSH_SADGE    ("Sadge",         0x6A8FBF),
    N_PLUSH_SCAREDAF ("Scared AF",     0xD4A843),
    N_PLUSH_SPOOKED  ("Spooked",       0xA06DB5),
    N_PLUSH_TRAUMATIZED("Traumatized", 0x7BAF87),
    N_PLUSH_UNAMUSED ("Unamused",      0x888888),
    N_PLUSH_WORRIED  ("Worried",       0xD4A870),
    N_PLUSH_WORRIEDAF("Worried AF",    0xDB8A3A);

    public final String displayName;
    public final int color;

    NPlushVariant(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    @Override public String displayName() { return displayName; }
    @Override public int color() { return color; }
}