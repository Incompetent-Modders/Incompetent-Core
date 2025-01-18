package com.incompetent_modders.incomp_core.core.def;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.DefaultAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.DefaultManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.DefaultClassPassiveEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

public record ClassType(boolean canCastSpells, int maxMana, boolean pacifist, boolean useClassSpecificTexture, ManaRegenCondition manaRegenCondition, ClassPassiveEffect passiveEffect, Ability ability, int passiveEffectTickFrequency, int abilityCooldown) {
    public static final Codec<ClassType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("can_cast_spells").forGetter(ClassType::canCastSpells),
            Codec.INT.fieldOf("max_mana").forGetter(ClassType::maxMana),
            Codec.BOOL.optionalFieldOf("pacifist", false).forGetter(ClassType::pacifist),
            Codec.BOOL.optionalFieldOf("use_class_specific_texture", false).forGetter(ClassType::useClassSpecificTexture),
            ManaRegenCondition.DIRECT_CODEC.fieldOf("mana_regen_condition").forGetter(ClassType::manaRegenCondition),
            ClassPassiveEffect.DIRECT_CODEC.fieldOf("passive_effect").forGetter(ClassType::passiveEffect),
            Ability.DIRECT_CODEC.fieldOf("ability").forGetter(ClassType::ability),
            Codec.INT.fieldOf("passive_effect_tick_frequency").forGetter(ClassType::passiveEffectTickFrequency),
            Codec.INT.fieldOf("ability_cooldown").forGetter(ClassType::abilityCooldown)
    ).apply(instance, ClassType::new));

    public static final Codec<Holder<ClassType>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.CLASS_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ClassType>> STREAM_CODEC;

    public static final Codec<ClassType> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("can_cast_spells").forGetter(ClassType::canCastSpells),
            Codec.INT.fieldOf("max_mana").forGetter(ClassType::maxMana),
            Codec.BOOL.optionalFieldOf("pacifist", false).forGetter(ClassType::pacifist),
            Codec.BOOL.optionalFieldOf("use_class_specific_texture", false).forGetter(ClassType::useClassSpecificTexture),
            ManaRegenCondition.DIRECT_CODEC.fieldOf("mana_regen_condition").forGetter(ClassType::manaRegenCondition),
            ClassPassiveEffect.DIRECT_CODEC.fieldOf("passive_effect").forGetter(ClassType::passiveEffect),
            Ability.DIRECT_CODEC.fieldOf("ability").forGetter(ClassType::ability),
            Codec.INT.fieldOf("passive_effect_tick_frequency").forGetter(ClassType::passiveEffectTickFrequency),
            Codec.INT.fieldOf("ability_cooldown").forGetter(ClassType::abilityCooldown)
    ).apply(instance, ClassType::new));

    public ClassType(boolean canCastSpells, int maxMana, boolean pacifist, boolean useClassSpecificTexture, ManaRegenCondition manaRegenCondition, ClassPassiveEffect passiveEffect, Ability ability, int passiveEffectTickFrequency, int abilityCooldown) {
        this.canCastSpells = canCastSpells;
        this.maxMana = maxMana;
        this.pacifist = pacifist;
        this.useClassSpecificTexture = useClassSpecificTexture;
        this.manaRegenCondition = manaRegenCondition;
        this.passiveEffect = passiveEffect;
        this.ability = ability;
        this.passiveEffectTickFrequency = passiveEffectTickFrequency;
        this.abilityCooldown = abilityCooldown;
    }

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.CLASS_TYPE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Component getDisplayName(ResourceLocation classType) {
        return Component.translatable("class_types." + classType.getNamespace() + "." + classType.getPath());
    }

    public static class Builder {
        private boolean canCastSpells = false;
        private int maxMana = 0;
        private boolean pacifist = false;
        private boolean useClassSpecificTexture = false;
        private ManaRegenCondition manaRegenCondition = new DefaultManaRegenCondition(1.0f);
        private ClassPassiveEffect passiveEffect = new DefaultClassPassiveEffect(false);
        private Ability ability = new DefaultAbility(false);
        private int passiveEffectTickFrequency = 0;
        private int abilityCooldown = 0;

        public Builder() {
        }

        public Builder(ClassTypeProperties properties) {
            this.canCastSpells = properties.canCastSpells();
            this.maxMana = properties.maxMana();
            this.pacifist = properties.pacifist();
            this.useClassSpecificTexture = properties.useClassSpecificTexture();
            this.manaRegenCondition = properties.manaRegenCondition();
            this.passiveEffect = properties.passiveEffect();
            this.ability = properties.ability();
            this.passiveEffectTickFrequency = properties.passiveEffectTickFrequency();
            this.abilityCooldown = properties.abilityCooldown();
        }

        public Builder canCastSpells(boolean canCastSpells) {
            this.canCastSpells = canCastSpells;
            return this;
        }

        public Builder maxMana(int maxMana) {
            this.maxMana = maxMana;
            return this;
        }

        public Builder pacifist(boolean pacifist) {
            this.pacifist = pacifist;
            return this;
        }

        public Builder useClassSpecificTexture(boolean useClassSpecificTexture) {
            this.useClassSpecificTexture = useClassSpecificTexture;
            return this;
        }

        public Builder manaRegenCondition(ManaRegenCondition manaRegenCondition) {
            this.manaRegenCondition = manaRegenCondition;
            return this;
        }

        public Builder passiveEffect(ClassPassiveEffect passiveEffect, int TickFrequency) {
            this.passiveEffect = passiveEffect;
            this.passiveEffectTickFrequency = TickFrequency;
            return this;
        }

        public Builder ability(Ability ability, int abilityCooldown) {
            this.ability = ability;
            this.abilityCooldown = abilityCooldown;
            return this;
        }

        public ClassType build() {
            return new ClassType(canCastSpells, maxMana, pacifist, useClassSpecificTexture, manaRegenCondition, passiveEffect, ability, passiveEffectTickFrequency, abilityCooldown);
        }
    }
}
