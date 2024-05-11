package com.incompetent_modders.incomp_core.api.json.species;

import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviour;
import com.incompetent_modders.incomp_core.registry.ModSpeciesBehaviourTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;

public record SpeciesProperties(SpeciesBehaviour behaviour, boolean invertHealAndHarm, DietType dietType, boolean keepOnDeath) {
    public static final Codec<SpeciesProperties> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                SpeciesBehaviour.DIRECT_CODEC.fieldOf("behaviour").forGetter(SpeciesProperties::behaviour),
                Codec.BOOL.optionalFieldOf("invert_heal_and_harm", false).forGetter(SpeciesProperties::invertHealAndHarm),
                DietType.CODEC.optionalFieldOf("diet_type", DietType.OMNIVORE).forGetter(SpeciesProperties::dietType),
                Codec.BOOL.optionalFieldOf("keep_on_death", true).forGetter(SpeciesProperties::keepOnDeath)
        ).apply(instance, SpeciesProperties::new);
    });
}
