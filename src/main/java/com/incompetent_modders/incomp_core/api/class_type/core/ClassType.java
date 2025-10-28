package com.incompetent_modders.incomp_core.api.class_type.core;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.class_type.ability.AbilityEntry;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttribute;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.DefaultAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.DefaultManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.DefaultClassPassiveEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public record ClassType(
        boolean useClassSpecificTexture,
        List<AbilityEntry> abilities,
        Map<ClassAttributeType<?>, ClassAttribute> attributes
) {

    public static final Codec<ClassType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("use_class_specific_texture", false).forGetter(ClassType::useClassSpecificTexture),
            AbilityEntry.DIRECT_CODEC.listOf().fieldOf("abilities").validate(abilities -> {
                List<ResourceLocation> usedIdentifiers = new ArrayList<>();
                for (AbilityEntry ability : abilities) {
                    if (!usedIdentifiers.contains(ability.identifier())) {
                        usedIdentifiers.add(ability.identifier());
                    } else {
                        return DataResult.error(() -> "Duplicate identifier " + ability.identifier());
                    }
                }
                return DataResult.success(abilities);
            }).forGetter(ClassType::abilities),
            ClassAttribute.MAPPED_CODEC.fieldOf("attributes").forGetter(ClassType::attributes)
    ).apply(instance, ClassType::new));

    public static final Codec<Holder<ClassType>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.CLASS_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ClassType>> STREAM_CODEC;

    public static final Codec<ClassType> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("use_class_specific_texture", false).forGetter(ClassType::useClassSpecificTexture),
            AbilityEntry.DIRECT_CODEC.listOf().fieldOf("abilities").validate(abilities -> {
                List<ResourceLocation> usedIdentifiers = new ArrayList<>();
                for (AbilityEntry ability : abilities) {
                    if (!usedIdentifiers.contains(ability.identifier())) {
                        usedIdentifiers.add(ability.identifier());
                    } else {
                        return DataResult.error(() -> "Duplicate identifier " + ability.identifier());
                    }
                }
                return DataResult.success(abilities);
            }).forGetter(ClassType::abilities),
            ClassAttribute.MAPPED_CODEC.fieldOf("attributes").forGetter(ClassType::attributes)
    ).apply(instance, ClassType::new));

    public ClassType(boolean useClassSpecificTexture, List<AbilityEntry> abilities, Map<ClassAttributeType<?>, ClassAttribute> attributes) {
        this.useClassSpecificTexture = useClassSpecificTexture;
        this.abilities = abilities;
        this.attributes = attributes;
    }

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.CLASS_TYPE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Component getDisplayName(ResourceLocation classType) {
        return Component.translatable("class_type." + classType.getNamespace() + "." + classType.getPath());
    }

    public boolean has(ClassAttributeType<?> attribute) {
        return attributes.containsKey(attribute);
    }

    public boolean has(Holder<ClassAttributeType<?>> attribute) {
        return has(attribute.value());
    }

    @SuppressWarnings("unchecked")
    public <T extends ClassAttribute> T get(ClassAttributeType<T> attribute) {
        if (has(attribute)) {
            return (T) attributes.get(attribute);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T, B extends T> List<B> getOfType(Class<T> clazz) {
        List<B> list = new ArrayList<>();
        for (ClassAttribute attribute : attributes.values()) {
            if (clazz.isInstance(attribute)) {
                list.add((B) attribute);
            }
        }
        return list;
    }

    public Map<ResourceLocation, AbilityEntry> getAbilities() {
        return this.abilities.stream().collect(Collectors.toMap(AbilityEntry::identifier, e -> e));
    }

    public AbilityEntry getAbility(ResourceLocation identifier) {
        if (!getAbilities().containsKey(identifier)) {
            IncompCore.LOGGER.error("ClassType does not have an ability named: '{}'", identifier);
            return null;
        }
        return getAbilities().get(identifier);
    }

    public static class Builder {
        private boolean useClassSpecificTexture = false;
        private List<AbilityEntry> abilities = new ArrayList<>();
        private Map<ClassAttributeType<?>, ClassAttribute> attributes = new HashMap<>();

        public Builder() {
        }

        public Builder useClassSpecificTexture(boolean useClassSpecificTexture) {
            this.useClassSpecificTexture = useClassSpecificTexture;
            return this;
        }

        public Builder abilities(AbilityEntry... abilities) {
            this.abilities.addAll(Arrays.asList(abilities));
            return this;
        }

        public Builder attributes(ClassAttribute... attributes) {
            for (ClassAttribute attribute : attributes) {
                this.attributes.put(attribute.getType(), attribute);
            }
            return this;
        }

        public ClassType build() {
            return new ClassType(useClassSpecificTexture, abilities, attributes);
        }
    }
}
