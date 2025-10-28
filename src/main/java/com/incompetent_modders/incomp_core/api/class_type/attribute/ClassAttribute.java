package com.incompetent_modders.incomp_core.api.class_type.attribute;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;

import java.util.Map;

public abstract class ClassAttribute {
    public static final Codec<Map<ClassAttributeType<?>, ClassAttribute>> MAPPED_CODEC = Codec.dispatchedMap(
            ModRegistries.CLASS_ATTRIBUTE_TYPE.byNameCodec(), ClassAttributeType::codec);

    public abstract ClassAttributeType<? extends ClassAttribute> getType();
}
