package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.common.util.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SpeciesType(ResourceLocation speciesID, boolean acceptAllSpecies) {
    public static final Codec<SpeciesType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", Utils.defaultSpecies).forGetter(SpeciesType::speciesID),
            Codec.BOOL.optionalFieldOf("accept_all_species", false).forGetter(SpeciesType::acceptAllSpecies)
    ).apply(instance, SpeciesType::new));
    
    public static final SpeciesType ANY = new SpeciesType(Utils.defaultSpecies, false);
    
    public SpeciesType(ResourceLocation speciesID) {
        this(speciesID, false);
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(speciesID);
        buf.writeBoolean(acceptAllSpecies);
    }
    
    public static SpeciesType decode(RegistryFriendlyByteBuf buf) {
        var id = buf.readResourceLocation();
        var acceptAll = buf.readBoolean();
        return new SpeciesType(id, acceptAll);
    }
}
