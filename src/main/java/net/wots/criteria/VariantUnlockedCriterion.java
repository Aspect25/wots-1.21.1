package net.wots.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

/**
 * Custom criterion: triggers when a player unlocks a plushie variant.
 *
 * In advancement JSON:
 * {
 *   "trigger": "wots:variant_unlocked",
 *   "conditions": {
 *     "variant": "UZI_PLUSH_OHNO"
 *   }
 * }
 *
 * The "variant" field can be:
 * - A specific full enum name: "UZI_PLUSH_OHNO" — matches only that variant
 * - Omitted — matches ANY variant unlock
 *
 * You can also use "variant_contains" for partial matching:
 * - "SADGE" — matches both "N_PLUSH_SADGE" and "UZI_PLUSH_SADGE"
 */
public class VariantUnlockedCriterion extends AbstractCriterion<VariantUnlockedCriterion.Conditions> {

    public record Conditions(
        Optional<LootContextPredicate> player,
        Optional<String> variant,
        Optional<String> variantContains
    ) implements AbstractCriterion.Conditions {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player")
                    .forGetter(Conditions::player),
                Codec.STRING.optionalFieldOf("variant")
                    .forGetter(Conditions::variant),
                Codec.STRING.optionalFieldOf("variant_contains")
                    .forGetter(Conditions::variantContains)
            ).apply(instance, Conditions::new)
        );

        public boolean matches(String unlockedEnumName) {
            if (variant.isPresent() && !variant.get().equals(unlockedEnumName)) {
                return false;
            }
            if (variantContains.isPresent() && !unlockedEnumName.contains(variantContains.get())) {
                return false;
            }
            return true;
        }
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    /**
     * Trigger this criterion for a player.
     * @param enumName Full enum name like "UZI_PLUSH_OHNO"
     */
    public void trigger(ServerPlayerEntity player, String enumName) {
        this.trigger(player, conditions -> conditions.matches(enumName));
    }
}
