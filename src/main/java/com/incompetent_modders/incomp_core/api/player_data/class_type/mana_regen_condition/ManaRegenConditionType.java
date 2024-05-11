package com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition;

import com.mojang.serialization.MapCodec;

public record ManaRegenConditionType<T extends ManaRegenCondition>(MapCodec<T> codec) {
}
