package com.incompetent_modders.incomp_core.api.json.potion.convert;

import com.incompetent_modders.incomp_core.IncompCore;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record Convert(ConvertCondition condition, ResourceLocation convertTo) {
    public static final ResourceLocation convertToNothing = ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "convert_to_nothing");
    public static final Codec<Convert> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConvertCondition.CODEC.fieldOf("condition").forGetter(Convert::condition),
            ResourceLocation.CODEC.fieldOf("convert_to").forGetter(Convert::convertTo)
    ).apply(instance, Convert::new));
    
    public static Convert EMPTY_CONVERT = new Convert(ConvertCondition.EMPTY_CONDITION, convertToNothing);
}
