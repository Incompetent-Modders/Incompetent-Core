package com.incompetent_modders.incomp_core.api.class_type.attribute;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record ClassAttributeType<T extends ClassAttribute>(Codec<T> codec) {

    public ResourceLocation getAttributeIdentifier() {
        return ModRegistries.CLASS_ATTRIBUTE_TYPE.getKey(this);
    }
}
