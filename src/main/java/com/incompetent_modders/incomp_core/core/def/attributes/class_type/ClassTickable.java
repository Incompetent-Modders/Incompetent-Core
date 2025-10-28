package com.incompetent_modders.incomp_core.core.def.attributes.class_type;

import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttribute;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface ClassTickable {
    void tick(Level level, LivingEntity entity);

    ClassAttributeType<? extends ClassAttribute> getType();
}
