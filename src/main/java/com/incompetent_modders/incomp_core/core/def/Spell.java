package com.incompetent_modders.incomp_core.core.def;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.conditions.*;
import com.incompetent_modders.incomp_core.core.def.conditions.SpeciesTypeCondition;
import com.incompetent_modders.incomp_core.core.def.conditions.ClassTypeCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public record Spell(Component description, SpellDefinition definition) {

    public static final Codec<Spell> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Spell::description),
            SpellDefinition.CODEC.fieldOf("definition").forGetter(Spell::definition)
    ).apply(instance, Spell::new));

    public static final Codec<Holder<Spell>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.SPELL);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Spell>> STREAM_CODEC;

    public static final Codec<Spell> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Spell::description),
            SpellDefinition.CODEC.fieldOf("definition").forGetter(Spell::definition)
    ).apply(instance, Spell::new));

    public Spell(Component description, SpellDefinition definition) {
        this.description = description;
        this.definition = definition;
    }


    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.SPELL);
    }

    public record SpellDefinition(SpellCategory category, double manaCost, int drawTime, SpellResults results, SoundEvent castSound, SpellConditions conditions) {
        public static final MapCodec<SpellDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SpellCategory.CODEC.optionalFieldOf("category", SpellCategory.UTILITY).forGetter(SpellDefinition::category),
                Codec.DOUBLE.optionalFieldOf("mana_cost", 0.0).forGetter(SpellDefinition::manaCost),
                Codec.INT.optionalFieldOf("draw_time", 0).forGetter(SpellDefinition::drawTime),
                SpellResults.CODEC.optionalFieldOf("results", SpellResults.EMPTY).forGetter(SpellDefinition::results),
                SoundEvent.DIRECT_CODEC.optionalFieldOf("cast_sound", SoundEvents.ALLAY_THROW).forGetter(SpellDefinition::castSound),
                SpellConditions.CODEC.optionalFieldOf("conditions", SpellConditions.EMPTY).forGetter(SpellDefinition::conditions)
                ).apply(instance, SpellDefinition::new));

        public SpellDefinition(SpellCategory category, double manaCost, int drawTime, SpellResults results, SoundEvent castSound, SpellConditions conditions) {
            this.category = category;
            this.manaCost = manaCost;
            this.drawTime = drawTime;
            this.conditions = conditions;
            this.results = results;
            this.castSound = castSound;
        }

        public static Builder builder(double manaCost, int drawTime) {
            return new Builder(manaCost, drawTime);
        }

        public static class Builder {
            private SpellCategory category = SpellCategory.UTILITY;
            private double manaCost = 0.0;
            private int drawTime = 0;
            private SpellResults results = SpellResults.EMPTY;
            private SoundEvent castSound = SoundEvents.ALLAY_THROW;
            private SpellConditions conditions = SpellConditions.EMPTY;

            public Builder (double manaCost, int drawTime) {
                this.manaCost = manaCost;
                this.drawTime = drawTime;
            }

            public Builder category(SpellCategory category) {
                this.category = category;
                return this;
            }

            public Builder conditions(SpellConditions conditions) {
                this.conditions = conditions;
                return this;
            }

            public Builder conditions(SpellConditions.Builder conditionsBuilder) {
                this.conditions = conditionsBuilder.build();
                return this;
            }

            public Builder results(SpellResults results) {
                this.results = results;
                return this;
            }

            public Builder castSound(SoundEvent castSound) {
                this.castSound = castSound;
                return this;
            }

            public SpellDefinition build() {
                return new SpellDefinition(category, manaCost, drawTime, results, castSound, conditions);
            }
        }
    }

    public record SpellConditions(CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType) {
        public static final Codec<SpellConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CatalystCondition.CODEC.optionalFieldOf("catalyst", CatalystCondition.EMPTY).forGetter(SpellConditions::catalyst),
                ClassTypeCondition.CODEC.optionalFieldOf("class", ClassTypeCondition.ANY).forGetter(SpellConditions::classType),
                SpeciesTypeCondition.CODEC.optionalFieldOf("species", SpeciesTypeCondition.ANY).forGetter(SpellConditions::speciesType)
        ).apply(instance, SpellConditions::new));

        public static final SpellConditions EMPTY = new SpellConditions(CatalystCondition.EMPTY, ClassTypeCondition.ANY, SpeciesTypeCondition.ANY);

        public boolean matches(CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType) {
            return this.catalyst == catalyst && this.classType == classType && this.speciesType == speciesType;
        }

        public boolean matches(SpellConditions conditions) {
            return this.catalyst == conditions.catalyst && this.classType == conditions.classType && this.speciesType == conditions.speciesType;
        }

        public SpellConditions(CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType) {
            this.catalyst = catalyst;
            this.classType = classType;
            this.speciesType = speciesType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private CatalystCondition catalyst = CatalystCondition.EMPTY;
            private ClassTypeCondition classType = ClassTypeCondition.ANY;
            private SpeciesTypeCondition speciesType = SpeciesTypeCondition.ANY;

            public Builder catalyst(CatalystCondition catalyst) {
                this.catalyst = catalyst;
                return this;
            }

            public Builder classType(ClassTypeCondition classType) {
                this.classType = classType;
                return this;
            }

            public Builder speciesType(SpeciesTypeCondition speciesType) {
                this.speciesType = speciesType;
                return this;
            }

            public SpellConditions build() {
                return new SpellConditions(catalyst, classType, speciesType);
            }
        }
    }
}
