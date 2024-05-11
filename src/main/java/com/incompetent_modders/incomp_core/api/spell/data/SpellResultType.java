package com.incompetent_modders.incomp_core.api.spell.data;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record SpellResultType<T extends SpellResult>(MapCodec<T> codec) {
    public ResourceLocation getSpellResultTypeIdentifier() {
        return ModRegistries.SPELL_RESULT_TYPE.getKey(this);
    }
}
