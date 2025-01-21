package com.incompetent_modders.incomp_core.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeEntry(Holder<Attribute> attributeHolder, AttributeModifier attributeModifier) {

    public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeEntry::attributeHolder),
            AttributeModifier.CODEC.fieldOf("modifier").forGetter(AttributeEntry::attributeModifier)
    ).apply(instance, AttributeEntry::new));


    public static AttributeEntry of(Holder<Attribute> attributeHolder, AttributeModifier attributeModifier) {
        return new AttributeEntry(attributeHolder, attributeModifier);
    }
}
