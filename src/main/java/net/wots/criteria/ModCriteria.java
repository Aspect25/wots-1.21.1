package net.wots.criteria;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.Identifier;
import net.wots.Wots;

public class ModCriteria {
    public static final VariantUnlockedCriterion VARIANT_UNLOCKED = new VariantUnlockedCriterion();

    public static void register() {
        CriteriaTriggers.register(Identifier.fromNamespaceAndPath(Wots.MOD_ID, "variant_unlocked").toString(), VARIANT_UNLOCKED);
        Wots.LOGGER.info("Registered WOTS advancement criteria");
    }
}
