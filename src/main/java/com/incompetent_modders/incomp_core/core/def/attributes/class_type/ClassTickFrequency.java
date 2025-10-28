package com.incompetent_modders.incomp_core.core.def.attributes.class_type;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttribute;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModClassAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;

import java.util.HashMap;
import java.util.Map;

public class ClassTickFrequency extends ClassAttribute {
    public static final Codec<Map<ClassAttributeType<?>, Integer>> FREQUENCY_CODEC = Codec.unboundedMap(
            ModRegistries.CLASS_ATTRIBUTE_TYPE.byNameCodec(), ExtraCodecs.POSITIVE_INT);

    public static final Codec<ClassTickFrequency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FREQUENCY_CODEC.fieldOf("frequencies").forGetter(att -> att.frequencies)
    ).apply(instance, ClassTickFrequency::new));

    public final Map<ClassAttributeType<?>, Integer> frequencies;

    public ClassTickFrequency(Map<ClassAttributeType<?>, Integer> frequencies) {
        this.frequencies = frequencies;
    }

    public int getFrequency(ClassAttributeType<?> speciesAttributeType) {
        return this.frequencies.getOrDefault(speciesAttributeType, 1);
    }

    @Override
    public ClassAttributeType<? extends ClassAttribute> getType() {
        return ModClassAttributes.TICK_FREQUENCY.get();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<ClassAttributeType<?>, Integer> frequencies = new HashMap<>();

        public Builder add(ClassAttributeType<?> speciesAttributeType, int frequency) {
            frequencies.put(speciesAttributeType, frequency);
            return this;
        }

        public Builder add(Holder<ClassAttributeType<?>> speciesAttributeType, int frequency) {
            frequencies.put(speciesAttributeType.value(), frequency);
            return this;
        }

        public ClassTickFrequency build() {
            return new ClassTickFrequency(frequencies);
        }
    }
}
