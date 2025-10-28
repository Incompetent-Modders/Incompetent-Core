package com.incompetent_modders.incomp_core.api.species.core;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.DefaultAbility;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public record SpeciesType(boolean keepOnDeath, Map<SpeciesAttributeType<?>, SpeciesAttribute> speciesAttributes) {

    public static final Codec<SpeciesType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("keep_on_death", true).forGetter(SpeciesType::keepOnDeath),
                    SpeciesAttribute.MAPPED_CODEC.optionalFieldOf("attributes", Map.of()).forGetter(SpeciesType::speciesAttributes)
            ).apply(instance, SpeciesType::new));

    public static final Codec<Holder<SpeciesType>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.SPECIES_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SpeciesType>> STREAM_CODEC;

    public static final Codec<SpeciesType> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("keep_on_death", true).forGetter(SpeciesType::keepOnDeath),
            SpeciesAttribute.MAPPED_CODEC.optionalFieldOf("attributes", Map.of()).forGetter(SpeciesType::speciesAttributes)
    ).apply(instance, SpeciesType::new));

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.SPECIES_TYPE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Component getDisplayName(ResourceLocation species) {
        return Component.translatable(species.toLanguageKey("species"));
    }

    public List<SpeciesAttribute> getAttributes() {
        return List.copyOf(speciesAttributes.values());
    }

    public boolean has(SpeciesAttributeType<?> attribute) {
        return speciesAttributes.containsKey(attribute);
    }

    public boolean has(Holder<SpeciesAttributeType<?>> attribute) {
        return has(attribute.value());
    }

    @SuppressWarnings("unchecked")
    public <T extends SpeciesAttribute> T get(SpeciesAttributeType<T> attribute) {
        if (has(attribute)) {
            return (T) speciesAttributes.get(attribute);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T, B extends T> List<B> getOfType(Class<T> clazz) {
        List<B> list = new ArrayList<>();
        for (SpeciesAttribute attribute : speciesAttributes.values()) {
            if (clazz.isInstance(attribute)) {
                list.add((B) attribute);
            }
        }
        return list;
    }

    public static class Builder {
        private boolean keepOnDeath = true;
        private final Map<SpeciesAttributeType<?>, SpeciesAttribute> attributes = new HashMap<>();

        public Builder() {
        }

        public Builder keepOnDeath(boolean keepOnDeath) {
            this.keepOnDeath = keepOnDeath;
            return this;
        }

        public Builder addAttributes(SpeciesAttribute... attributes) {
            for (SpeciesAttribute attribute : attributes) {
                this.attributes.put(attribute.getType(), attribute);
            }
            return this;
        }

        public SpeciesType build() {
            return new SpeciesType(keepOnDeath, attributes);
        }
    }
}
