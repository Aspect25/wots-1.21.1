package net.wots.recipe;

import net.wots.Wots;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class CustomStonecuttingRecipe implements Recipe<SingleStackRecipeInput> {

    private final String group;
    private final Ingredient ingredient;
    private final ItemStack result;

    public CustomStonecuttingRecipe(String group, Ingredient ingredient, ItemStack result) {
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) { return true; }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) { return this.result; }

    @Override
    public RecipeSerializer<?> getSerializer() { return Wots.CUSTOM_STONECUTTING_SERIALIZER; }

    @Override
    public RecipeType<?> getType() { return Wots.CUSTOM_STONECUTTING_TYPE; }

    @Override
    public String getGroup() { return this.group; }

    public Ingredient getIngredient() { return ingredient; }
    public ItemStack getResultStack() { return result; }

    public static class Serializer implements RecipeSerializer<CustomStonecuttingRecipe> {

        public static final MapCodec<CustomStonecuttingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "")
                                .forGetter(CustomStonecuttingRecipe::getGroup),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .fieldOf("ingredient")
                                .forGetter(CustomStonecuttingRecipe::getIngredient),
                        ItemStack.VALIDATED_CODEC
                                .fieldOf("result")
                                .forGetter(CustomStonecuttingRecipe::getResultStack)
                ).apply(instance, CustomStonecuttingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, CustomStonecuttingRecipe> PACKET_CODEC =
                PacketCodec.tuple(
                        PacketCodecs.STRING, CustomStonecuttingRecipe::getGroup,
                        Ingredient.PACKET_CODEC, CustomStonecuttingRecipe::getIngredient,
                        ItemStack.PACKET_CODEC, CustomStonecuttingRecipe::getResultStack,
                        CustomStonecuttingRecipe::new
                );

        @Override
        public MapCodec<CustomStonecuttingRecipe> codec() { return CODEC; }

        @Override
        public PacketCodec<RegistryByteBuf, CustomStonecuttingRecipe> packetCodec() { return PACKET_CODEC; }
    }
}