package com.incompetent_modders.incomp_core.api.json.class_type;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ClassTypeProperties(boolean canCastSpells, int maxMana, boolean pacifist, boolean useClassSpecificTexture, ManaRegenCondition manaRegenCondition, ClassPassiveEffect passiveEffect, ClassAbility ability, int passiveEffectTickFrequency, int abilityCooldown) {
    public static final Codec<ClassTypeProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("can_cast_spells").forGetter(ClassTypeProperties::canCastSpells),
            Codec.INT.fieldOf("max_mana").forGetter(ClassTypeProperties::maxMana),
            Codec.BOOL.fieldOf("pacifist").forGetter(ClassTypeProperties::pacifist),
            Codec.BOOL.fieldOf("use_class_specific_texture").forGetter(ClassTypeProperties::useClassSpecificTexture),
            ManaRegenCondition.DIRECT_CODEC.fieldOf("mana_regen_condition").forGetter(ClassTypeProperties::manaRegenCondition),
            ClassPassiveEffect.DIRECT_CODEC.fieldOf("passive_effect").forGetter(ClassTypeProperties::passiveEffect),
            ClassAbility.DIRECT_CODEC.fieldOf("ability").forGetter(ClassTypeProperties::ability),
            Codec.INT.fieldOf("passive_effect_tick_frequency").forGetter(ClassTypeProperties::passiveEffectTickFrequency),
            Codec.INT.fieldOf("ability_cooldown").forGetter(ClassTypeProperties::abilityCooldown)
    ).apply(instance, ClassTypeProperties::new));
}
