package com.incompetent_modders.incomp_core.api.json.species.diet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record DietProperties(TagKey<Item> ableToConsume, boolean ignoreHungerFromFood) {
    public static final MapCodec<DietProperties> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                TagKey.codec(Registries.ITEM).fieldOf("able_to_consume").forGetter(DietProperties::ableToConsume),
                Codec.BOOL.optionalFieldOf("ignore_hunger_from_food", false).forGetter(DietProperties::ignoreHungerFromFood)
                ).apply(instance, DietProperties::new);
    });
}
