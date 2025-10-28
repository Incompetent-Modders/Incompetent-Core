package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record ConvertToClassType(ConvertCondition condition, ResourceKey<ClassType> convertTo) {
    public static final ResourceKey<ClassType> convertToNothing = ResourceKey.create(ModRegistries.Keys.CLASS_TYPE, ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "convert_to_nothing"));
    public static final Codec<ConvertToClassType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConvertCondition.CODEC.fieldOf("condition").forGetter(ConvertToClassType::condition),
            ResourceKey.codec(ModRegistries.Keys.CLASS_TYPE).fieldOf("convert_to").forGetter(ConvertToClassType::convertTo)
    ).apply(instance, ConvertToClassType::new));

    public static ConvertToClassType EMPTY_CONVERT = new ConvertToClassType(ConvertCondition.EMPTY_CONDITION, convertToNothing);
}
