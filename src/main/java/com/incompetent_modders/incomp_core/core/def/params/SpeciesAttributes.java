package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.common.util.AttributeEntry;
import com.incompetent_modders.incomp_core.common.util.AttributeModifierEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record SpeciesAttributes(List<AttributeModifierEntry> attributeModifiers, List<AttributeEntry> attributes) {

    public static final Codec<SpeciesAttributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AttributeModifierEntry.CODEC.listOf().fieldOf("modifiers").forGetter(SpeciesAttributes::attributeModifiers),
            AttributeEntry.CODEC.listOf().fieldOf("base_values").forGetter(SpeciesAttributes::attributes)
    ).apply(instance, SpeciesAttributes::new));

    public static SpeciesAttributes of(List<AttributeModifierEntry> attributes, List<AttributeEntry> baseValues) {
        return new SpeciesAttributes(attributes, baseValues);
    }

    public static SpeciesAttributes createEmpty() {
        return new SpeciesAttributes(List.of(), List.of());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<AttributeModifierEntry> modifiers = new ArrayList<>();
        private final List<AttributeEntry> baseValues = new ArrayList<>();

        public Builder() {

        }

        public Builder(List<AttributeModifierEntry> attributes, List<AttributeEntry> baseValues) {
            this.modifiers.addAll(attributes);
            this.baseValues.addAll(baseValues);
        }

        public Builder addModifier(AttributeModifierEntry attribute) {
            this.modifiers.add(attribute);
            return this;
        }

        public Builder addModifier(AttributeModifierEntry... attributes) {
            this.modifiers.addAll(Arrays.asList(attributes));
            return this;
        }

        public Builder setBaseValue(AttributeEntry attribute) {
            this.baseValues.add(attribute);
            return this;
        }

        public Builder setBaseValue(AttributeEntry... attributes) {
            this.baseValues.addAll(Arrays.asList(attributes));
            return this;
        }

        public SpeciesAttributes build() {
            return new SpeciesAttributes(modifiers, baseValues);
        }
    }
}
