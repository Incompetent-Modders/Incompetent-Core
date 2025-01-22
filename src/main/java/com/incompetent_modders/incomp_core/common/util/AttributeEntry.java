package com.incompetent_modders.incomp_core.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record AttributeEntry(Holder<Attribute> attributeHolder, double baseValue) {
    public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeEntry::attributeHolder),
            Codec.DOUBLE.fieldOf("base_value").forGetter(AttributeEntry::baseValue)
    ).apply(instance, AttributeEntry::new));


    public static AttributeEntry of(Holder<Attribute> attributeHolder, double baseValue) {
        return new AttributeEntry(attributeHolder, baseValue);
    }
}
