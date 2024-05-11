package com.incompetent_modders.incomp_core.api.player_data.class_type.ability;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record ClassAbilityType<T extends ClassAbility>(MapCodec<T> codec) {
    public ResourceLocation getClassAbilityTypeIdentifier() {
        return ModRegistries.CLASS_ABILITY_TYPE.getKey(this);
    }
}
