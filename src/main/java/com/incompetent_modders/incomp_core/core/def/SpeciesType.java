package com.incompetent_modders.incomp_core.core.def;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.DefaultAbility;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.DefaultSpeciesBehaviour;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviour;
import com.incompetent_modders.incomp_core.common.registry.ModDiets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record SpeciesType(SpeciesBehaviour behaviour, boolean invertHealAndHarm, Holder<Diet> dietType, boolean keepOnDeath, Ability ability, int abilityCooldown) {
    public static final Codec<SpeciesType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    SpeciesBehaviour.DIRECT_CODEC.fieldOf("behaviour").forGetter(SpeciesType::behaviour),
                    Codec.BOOL.optionalFieldOf("invert_heal_and_harm", false).forGetter(SpeciesType::invertHealAndHarm),
                    Diet.CODEC.fieldOf("diet").forGetter(SpeciesType::dietType),
                    Codec.BOOL.optionalFieldOf("keep_on_death", true).forGetter(SpeciesType::keepOnDeath),
                    //EnchantmentWeaknessProperties.CODEC.listOf().optionalFieldOf("enchant_weaknesses", NonNullList.create()).forGetter(SpeciesType::enchantWeaknesses),
                    Ability.DIRECT_CODEC.fieldOf("ability").forGetter(SpeciesType::ability),
                    Codec.INT.fieldOf("ability_cooldown").forGetter(SpeciesType::abilityCooldown)
            ).apply(instance, SpeciesType::new));

    public static final Codec<Holder<SpeciesType>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.SPECIES_TYPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SpeciesType>> STREAM_CODEC;

    public static final Codec<SpeciesType> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpeciesBehaviour.DIRECT_CODEC.fieldOf("behaviour").forGetter(SpeciesType::behaviour),
            Codec.BOOL.optionalFieldOf("invert_heal_and_harm", false).forGetter(SpeciesType::invertHealAndHarm),
            Diet.CODEC.fieldOf("diet").forGetter(SpeciesType::dietType),
            Codec.BOOL.optionalFieldOf("keep_on_death", true).forGetter(SpeciesType::keepOnDeath),
            //EnchantmentWeaknessProperties.CODEC.listOf().optionalFieldOf("enchant_weaknesses", NonNullList.create()).forGetter(SpeciesType::enchantWeaknesses),
            Ability.DIRECT_CODEC.fieldOf("ability").forGetter(SpeciesType::ability),
            Codec.INT.fieldOf("ability_cooldown").forGetter(SpeciesType::abilityCooldown)
    ).apply(instance, SpeciesType::new));

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.SPECIES_TYPE);
    }

    public static Builder builder(BootstrapContext<SpeciesType> context) {
        return new Builder(context);
    }

    public static Component getDisplayName(ResourceLocation species) {
        return Component.translatable("species." + species.getNamespace() + "." + species.getPath());
    }

    public static class Builder {
        private final HolderGetter<Diet> diets;
        private SpeciesBehaviour behaviour = new DefaultSpeciesBehaviour(false);
        private boolean invertPotionHealAndHarm = false;
        private ResourceKey<Diet> dietType = ModDiets.OMNIVORE;
        private boolean keepOnDeath = true;
        private Ability ability = new DefaultAbility(false);
        private int abilityCooldown = 0;

        public Builder(BootstrapContext<SpeciesType> context) {
            this.diets = context.lookup(ModRegistries.Keys.DIET);
        }

        public Builder behaviour(SpeciesBehaviour behaviour) {
            this.behaviour = behaviour;
            return this;
        }

        public Builder invertPotionHealAndHarm() {
            this.invertPotionHealAndHarm = true;
            return this;
        }

        public Builder diet(ResourceKey<Diet> dietType) {
            this.dietType = dietType;
            return this;
        }

        public Builder keepOnDeath(boolean keepOnDeath) {
            this.keepOnDeath = keepOnDeath;
            return this;
        }

        public Builder ability(Ability ability, int cooldown) {
            this.ability = ability;
            this.abilityCooldown = cooldown;
            return this;
        }

        public SpeciesType build() {
            return new SpeciesType(behaviour, invertPotionHealAndHarm, diets.getOrThrow(dietType), keepOnDeath, ability, abilityCooldown);
        }
    }
}
