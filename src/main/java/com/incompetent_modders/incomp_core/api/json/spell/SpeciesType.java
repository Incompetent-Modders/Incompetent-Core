package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SpeciesType(ResourceLocation speciesID, boolean acceptAllSpecies) {
    public static final Codec<SpeciesType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", CommonUtils.defaultSpecies).forGetter(SpeciesType::speciesID),
            Codec.BOOL.optionalFieldOf("accept_all_species", false).forGetter(SpeciesType::acceptAllSpecies)
    ).apply(instance, SpeciesType::new));
}
