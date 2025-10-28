package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.common.util.AttributeEntry;
import com.incompetent_modders.incomp_core.common.util.AttributeModifierEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityAttributeAttribute extends SpeciesAttribute {

    public static final Codec<EntityAttributeAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AttributeModifierEntry.CODEC.listOf().fieldOf("modifiers").forGetter(att -> att.attributeModifiers),
            AttributeEntry.CODEC.listOf().fieldOf("base_values").forGetter(att -> att.baseAttributes)
    ).apply(instance, EntityAttributeAttribute::new));

    public final List<AttributeModifierEntry> attributeModifiers;
    public final List<AttributeEntry> baseAttributes;

    public EntityAttributeAttribute(List<AttributeModifierEntry> attributeModifiers, List<AttributeEntry> baseAttributes) {
        this.attributeModifiers = attributeModifiers;
        this.baseAttributes = baseAttributes;
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.ENTITY_ATTRIBUTES.get();
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

        public static AttributeModifierEntry createAttributeModifier(Holder<Attribute> attributeHolder, double value, AttributeModifier.Operation operation) {
            String name = "species_%s_modifier".formatted(attributeHolder.getKey().location().getPath().replace(".", "_"));
            AttributeModifier attributeModifier = new AttributeModifier(IncompCore.makeId(name), value, operation);
            return AttributeModifierEntry.of(attributeHolder, attributeModifier);
        }

        public static AttributeModifierEntry createAttributeModifier(Holder<Attribute> attributeHolder, double value, AttributeModifier.Operation operation, LootItemCondition condition) {
            String name = "species_%s_modifier".formatted(attributeHolder.getKey().location().getPath().replace(".", "_"));
            AttributeModifier attributeModifier = new AttributeModifier(IncompCore.makeId(name), value, operation);
            return AttributeModifierEntry.of(attributeHolder, attributeModifier, condition);
        }

        public EntityAttributeAttribute build() {
            return new EntityAttributeAttribute(modifiers, baseValues);
        }
    }
}
