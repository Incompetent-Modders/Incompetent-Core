package com.incompetent_modders.incomp_core.api.json.species;

import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviour;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SpeciesAttributes(float maxHealth,
                                float attackDamage, float attackKnockback,
                                float moveSpeed,
                                float armour, float luck,
                                float blockInteractionRange, float entityInteractionRange,
                                float gravity, float jumpStrength,
                                float knockbackResistance, float safeFallDistance,
                                float scale, float stepHeight,
                                float armourToughness) {
    public static final Codec<SpeciesAttributes> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.optionalFieldOf("max_health", 20.0f).forGetter(SpeciesAttributes::maxHealth),
                Codec.FLOAT.optionalFieldOf("attack_damage", 1.0f).forGetter(SpeciesAttributes::attackDamage),
                Codec.FLOAT.optionalFieldOf("attack_knockback", 0.0f).forGetter(SpeciesAttributes::attackKnockback),
                Codec.FLOAT.optionalFieldOf("move_speed", 0.1f).forGetter(SpeciesAttributes::moveSpeed),
                Codec.FLOAT.optionalFieldOf("armour", 0.0f).forGetter(SpeciesAttributes::armour),
                Codec.FLOAT.optionalFieldOf("luck", 0.0f).forGetter(SpeciesAttributes::luck),
                Codec.FLOAT.optionalFieldOf("block_interaction_range", 4.5f).forGetter(SpeciesAttributes::blockInteractionRange),
                Codec.FLOAT.optionalFieldOf("entity_interaction_range", 3.0f).forGetter(SpeciesAttributes::entityInteractionRange),
                Codec.FLOAT.optionalFieldOf("gravity", 0.08f).forGetter(SpeciesAttributes::gravity),
                Codec.FLOAT.optionalFieldOf("jump_strength", 0.42f).forGetter(SpeciesAttributes::jumpStrength),
                Codec.FLOAT.optionalFieldOf("knockback_resistance", 0.0f).forGetter(SpeciesAttributes::knockbackResistance),
                Codec.FLOAT.optionalFieldOf("safe_fall_distance", 3.0f).forGetter(SpeciesAttributes::safeFallDistance),
                Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(SpeciesAttributes::scale),
                Codec.FLOAT.optionalFieldOf("step_height", 0.6f).forGetter(SpeciesAttributes::stepHeight),
                Codec.FLOAT.optionalFieldOf("armour_toughness", 0.0f).forGetter(SpeciesAttributes::armourToughness)
        ).apply(instance, SpeciesAttributes::new);
    });
}
