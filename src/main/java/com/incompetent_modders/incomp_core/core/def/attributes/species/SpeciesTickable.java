package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface SpeciesTickable {

    void tick(Level level, LivingEntity entity);

    SpeciesAttributeType<? extends SpeciesAttribute> getType();
}
