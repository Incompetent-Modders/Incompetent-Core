package com.incompetent_modders.incomp_core.api.json.species.diet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public record DietProperties(NonNullList<Ingredient> ableToConsume, boolean ignoreHungerFromFood) {
    public static final MapCodec<DietProperties> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("able_to_consume").flatXmap((ingredientList) -> {
                Ingredient[] aingredient = ingredientList.toArray(Ingredient[]::new);
                if (aingredient.length == 0) {
                    return DataResult.error(() -> "No inputs for diet");
                } else {
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter(DietProperties::ableToConsume),
            Codec.BOOL.optionalFieldOf("ignore_hunger_from_food", false).forGetter(DietProperties::ignoreHungerFromFood)
            ).apply(instance, DietProperties::new));
}
