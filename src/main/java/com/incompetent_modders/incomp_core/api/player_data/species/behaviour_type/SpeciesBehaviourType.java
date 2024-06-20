package com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record SpeciesBehaviourType<T extends SpeciesBehaviour>(MapCodec<T> codec) {
    public ResourceLocation getBehaviourTypeIdentifier() {
        return ModRegistries.SPECIES_BEHAVIOUR_TYPE.getKey(this);
    }
    
    public static SpeciesBehaviourType<?> fromIdentifier(ResourceLocation identifier) {
        return ModRegistries.SPECIES_BEHAVIOUR_TYPE.get(identifier);
    }
}
