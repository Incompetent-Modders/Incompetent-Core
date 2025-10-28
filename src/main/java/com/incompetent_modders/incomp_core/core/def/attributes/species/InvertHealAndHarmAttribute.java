package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.mojang.serialization.Codec;

public class InvertHealAndHarmAttribute extends SpeciesAttribute {

    public static final Codec<InvertHealAndHarmAttribute> CODEC = Codec.unit(InvertHealAndHarmAttribute::new);

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.INVERT_HEAL_HARM.get();
    }
}
