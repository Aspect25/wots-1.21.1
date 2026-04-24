package net.wots.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * Custom criterion: triggers when a player unlocks a plushie variant.
 */
public class VariantUnlockedCriterion extends SimpleCriterionTrigger<VariantUnlockedCriterion.Conditions> {

    public record Conditions(
        Optional<ContextAwarePredicate> player,
        Optional<String> variant,
        Optional<String> variantContains
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
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
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    /**
     * Trigger this criterion for a player.
     * @param enumName Full enum name like "UZI_PLUSH_OHNO"
     */
    public void trigger(ServerPlayer player, String enumName) {
        this.trigger(player, conditions -> conditions.matches(enumName));
    }
}
