package net.wots.criteria;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.Identifier;
import net.wots.Wots;

public class ModCriteria {
    public static final VariantUnlockedCriterion VARIANT_UNLOCKED = new VariantUnlockedCriterion();

    public static void register() {
        Criteria.register(Identifier.of(Wots.MOD_ID, "variant_unlocked").toString(), VARIANT_UNLOCKED);
        Wots.LOGGER.info("Registered WOTS advancement criteria");
    }
}
