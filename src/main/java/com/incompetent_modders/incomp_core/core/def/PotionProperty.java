package com.incompetent_modders.incomp_core.core.def;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.params.ConvertToClassType;
import com.incompetent_modders.incomp_core.core.def.params.ConvertToSpeciesType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

public record PotionProperty(Holder<MobEffect> effect, float manaCostModifier, float manaRegenModifier, ConvertToSpeciesType convertToSpeciesOnFinish, ConvertToClassType convertToClassOnFinish) {
    public static final Codec<PotionProperty> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(PotionProperty::effect),
            Codec.FLOAT.optionalFieldOf("mana_cost_modifier", 1.0F).forGetter(PotionProperty::manaCostModifier),
            Codec.FLOAT.optionalFieldOf("mana_regen_modifier", 1.0F).forGetter(PotionProperty::manaRegenModifier),
            ConvertToSpeciesType.CODEC.optionalFieldOf("convert_to_species", ConvertToSpeciesType.EMPTY_CONVERT).forGetter(PotionProperty::convertToSpeciesOnFinish),
            ConvertToClassType.CODEC.optionalFieldOf("convert_to_class", ConvertToClassType.EMPTY_CONVERT).forGetter(PotionProperty::convertToClassOnFinish)
    ).apply(instance, PotionProperty::new));

    public static final Codec<Holder<PotionProperty>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.POTION_PROPERTY);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PotionProperty>> STREAM_CODEC;

    public static final Codec<PotionProperty> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(PotionProperty::effect),
            Codec.FLOAT.optionalFieldOf("mana_cost_modifier", 1.0F).forGetter(PotionProperty::manaCostModifier),
            Codec.FLOAT.optionalFieldOf("mana_regen_modifier", 1.0F).forGetter(PotionProperty::manaRegenModifier),
            ConvertToSpeciesType.CODEC.optionalFieldOf("convert_to_species", ConvertToSpeciesType.EMPTY_CONVERT).forGetter(PotionProperty::convertToSpeciesOnFinish),
            ConvertToClassType.CODEC.optionalFieldOf("convert_to_class", ConvertToClassType.EMPTY_CONVERT).forGetter(PotionProperty::convertToClassOnFinish)
    ).apply(instance, PotionProperty::new));

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.POTION_PROPERTY);
    }

    public static Builder builder(Holder<MobEffect> effect) {
        return new Builder(effect);
    }

    public void applyConverts(Player player) {
        boolean shouldConvertSpecies = convertToSpeciesOnFinish.condition().shouldConvert(player) && !convertToSpeciesOnFinish.convertTo().equals(ConvertToSpeciesType.convertToNothing);
        boolean shouldConvertClass = convertToClassOnFinish.condition().shouldConvert(player) && !convertToClassOnFinish.convertTo().equals(ConvertToClassType.convertToNothing);

        if (shouldConvertSpecies) {
            PlayerDataHelper.setSpeciesType(player, convertToSpeciesOnFinish.convertTo());
        }
        if (shouldConvertClass) {
            PlayerDataHelper.setClassType(player, convertToClassOnFinish.convertTo());
        }
    }

    public static class Builder {
        private final Holder<MobEffect> effect;
        private float manaCostModifier = 1.0F;
        private float manaRegenModifier = 1.0F;
        private ConvertToSpeciesType convertToSpeciesOnFinish = ConvertToSpeciesType.EMPTY_CONVERT;
        private ConvertToClassType convertToClassOnFinish = ConvertToClassType.EMPTY_CONVERT;

        public Builder(Holder<MobEffect> effect) {
            this.effect = effect;
        }

        public Builder manaCostModifier(float manaCostModifier) {
            this.manaCostModifier = manaCostModifier;
            return this;
        }

        public Builder manaRegenModifier(float manaRegenModifier) {
            this.manaRegenModifier = manaRegenModifier;
            return this;
        }

        public Builder convertToSpeciesOnFinish(ConvertToSpeciesType convertToSpeciesOnFinish) {
            this.convertToSpeciesOnFinish = convertToSpeciesOnFinish;
            return this;
        }

        public Builder convertToClassOnFinish(ConvertToClassType convertToClassOnFinish) {
            this.convertToClassOnFinish = convertToClassOnFinish;
            return this;
        }

        public PotionProperty build() {
            return new PotionProperty(effect, manaCostModifier, manaRegenModifier, convertToSpeciesOnFinish, convertToClassOnFinish);
        }
    }
}
