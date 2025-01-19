package com.incompetent_modders.incomp_core.core.def.conditions;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record ConvertToSpeciesType(ConvertCondition condition, ResourceKey<SpeciesType> convertTo) {
    public static final ResourceKey<SpeciesType> convertToNothing = ResourceKey.create(ModRegistries.Keys.SPECIES_TYPE, ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "convert_to_nothing"));
    public static final Codec<ConvertToSpeciesType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConvertCondition.CODEC.fieldOf("condition").forGetter(ConvertToSpeciesType::condition),
            ResourceKey.codec(ModRegistries.Keys.SPECIES_TYPE).fieldOf("convert_to").forGetter(ConvertToSpeciesType::convertTo)
    ).apply(instance, ConvertToSpeciesType::new));

    public static ConvertToSpeciesType EMPTY_CONVERT = new ConvertToSpeciesType(ConvertCondition.EMPTY_CONDITION, convertToNothing);
}
