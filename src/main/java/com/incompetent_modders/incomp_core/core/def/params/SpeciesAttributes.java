package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.common.util.AttributeEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record SpeciesAttributes(List<AttributeEntry> attributes) {

    public static final Codec<SpeciesAttributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AttributeEntry.CODEC.listOf().fieldOf("modifiers").forGetter(SpeciesAttributes::attributes)
    ).apply(instance, SpeciesAttributes::new));

    public static SpeciesAttributes of(List<AttributeEntry> attributes) {
        return new SpeciesAttributes(attributes);
    }

    public static SpeciesAttributes createEmpty() {
        return new SpeciesAttributes(List.of());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<AttributeEntry> attributes = new ArrayList<>();

        public Builder() {

        }

        public Builder(List<AttributeEntry> attributes) {
            this.attributes.addAll(attributes);
        }

        public Builder addAttribute(AttributeEntry attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public Builder addAttribute(AttributeEntry... attributes) {
            this.attributes.addAll(Arrays.asList(attributes));
            return this;
        }

        public SpeciesAttributes build() {
            return new SpeciesAttributes(attributes);
        }
    }
}
