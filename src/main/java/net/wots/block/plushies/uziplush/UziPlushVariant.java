package net.wots.block.plushies.uziplush;

public enum UziPlushVariant {
    UZI_PLUSH          ("Default",       0x9B8BB4),
    UZI_PLUSH_SADGE    ("Sadge",         0x6A8FBF),
    UZI_PLUSH_SCAREDAF ("Scared AF",     0xD4A843),
    UZI_PLUSH_SPOOKED  ("Spooked",       0xA06DB5),
    UZI_PLUSH_TRAUMATIZED("Traumatized", 0x7BAF87),
    UZI_PLUSH_UNAMUSED ("Unamused",      0x888888),
    UZI_PLUSH_ANGY     ("Angy",          0xC94C4C),
    UZI_PLUSH_ANGYAF   ("Angy AF",       0xFF2222),
    UZI_PLUSH_DRUNK    ("Drunk",         0xC47AB0),
    UZI_PLUSH_HAPPY    ("Happy",         0xF0C030),
    UZI_PLUSH_WORRIEDAF("Worried AF",    0xDB8A3A),
    UZI_PLUSH_WORRIED  ("Worried",       0xD4A870),
    UZI_PLUSH_OHNO     ("Oh No",         0xE05A80);

    public final String displayName;
    public final int color;

    UziPlushVariant(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }
}