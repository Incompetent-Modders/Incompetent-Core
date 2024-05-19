package com.incompetent_modders.incomp_core.api.player_data.class_type.ability;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record AbilityType<T extends Ability>(MapCodec<T> codec) {
    public ResourceLocation getAbilityTypeIdentifier() {
        return ModRegistries.ABILITY_TYPE.getKey(this);
    }
}
