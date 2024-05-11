package com.incompetent_modders.incomp_core.api.player_data.class_type.passive;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbility;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record ClassPassiveEffectType<T extends ClassPassiveEffect>(MapCodec<T> codec) {
    public ResourceLocation getClassPassiveEffectTypeIdentifier() {
        return ModRegistries.CLASS_PASSIVE_EFFECT_TYPE.getKey(this);
    }
}
