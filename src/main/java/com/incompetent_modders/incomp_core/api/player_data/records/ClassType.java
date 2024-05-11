package com.incompetent_modders.incomp_core.api.player_data.records;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ClassType(boolean canCastSpells, int maxMana, boolean pacifist, boolean useClassSpecificTexture, ClassAbility ability, ClassPassiveEffect passiveEffect) {
    public static final Codec<ClassType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("can_cast_spells").forGetter(ClassType::canCastSpells),
            Codec.INT.fieldOf("max_mana").forGetter(ClassType::maxMana),
            Codec.BOOL.fieldOf("pacifist").forGetter(ClassType::pacifist),
            Codec.BOOL.fieldOf("use_class_specific_texture").forGetter(ClassType::useClassSpecificTexture),
            ClassAbility.DIRECT_CODEC.optionalFieldOf("ability", null).forGetter(ClassType::ability),
            ClassPassiveEffect.DIRECT_CODEC.optionalFieldOf("passive_effect", null).forGetter(ClassType::passiveEffect)
    ).apply(instance, ClassType::new));
    
    
    
    
    
}
