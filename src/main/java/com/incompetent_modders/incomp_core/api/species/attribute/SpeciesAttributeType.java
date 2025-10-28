package com.incompetent_modders.incomp_core.api.species.attribute;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record SpeciesAttributeType<T extends SpeciesAttribute>(Codec<T> codec) {

    public ResourceLocation getAttributeIdentifier() {
        return ModRegistries.SPECIES_ATTRIBUTE_TYPE.getKey(this);
    }

}
