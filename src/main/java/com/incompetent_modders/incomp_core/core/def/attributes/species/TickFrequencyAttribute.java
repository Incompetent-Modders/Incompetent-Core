package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.Map;

public class TickFrequencyAttribute extends SpeciesAttribute {
    public static final Codec<Map<SpeciesAttributeType<?>, Integer>> FREQUENCY_CODEC = Codec.unboundedMap(
            ModRegistries.SPECIES_ATTRIBUTE_TYPE.byNameCodec(), ExtraCodecs.POSITIVE_INT);

    public static final Codec<TickFrequencyAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FREQUENCY_CODEC.fieldOf("frequencies").forGetter(att -> att.frequencies)
    ).apply(instance, TickFrequencyAttribute::new));

    public final Map<SpeciesAttributeType<?>, Integer> frequencies;

    public TickFrequencyAttribute(Map<SpeciesAttributeType<?>, Integer> frequencies) {
        this.frequencies = frequencies;
    }

    public int getFrequency(SpeciesAttributeType<?> speciesAttributeType) {
        return this.frequencies.getOrDefault(speciesAttributeType, 1);
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.TICK_FREQUENCY.get();
    }
}
