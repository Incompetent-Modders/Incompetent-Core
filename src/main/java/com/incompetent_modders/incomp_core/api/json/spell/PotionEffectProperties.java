package com.incompetent_modders.incomp_core.api.json.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PotionEffectProperties(float manaCostModifier, float manaRegenModifier) {
    public static final Codec<PotionEffectProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("mana_cost_modifier").forGetter(PotionEffectProperties::manaCostModifier),
            Codec.FLOAT.fieldOf("mana_regen_modifier").forGetter(PotionEffectProperties::manaRegenModifier)
    ).apply(instance, PotionEffectProperties::new));
}
