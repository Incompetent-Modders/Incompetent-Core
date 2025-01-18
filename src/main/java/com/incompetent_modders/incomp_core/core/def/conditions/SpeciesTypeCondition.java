package com.incompetent_modders.incomp_core.core.def.conditions;

import com.incompetent_modders.incomp_core.common.util.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SpeciesTypeCondition(ResourceLocation speciesID, boolean acceptAllSpecies) {
    public static final Codec<SpeciesTypeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", Utils.defaultSpecies).forGetter(SpeciesTypeCondition::speciesID),
            Codec.BOOL.optionalFieldOf("accept_all_species", false).forGetter(SpeciesTypeCondition::acceptAllSpecies)
    ).apply(instance, SpeciesTypeCondition::new));
    
    public static final SpeciesTypeCondition ANY = new SpeciesTypeCondition(Utils.defaultSpecies, false);
    
    public SpeciesTypeCondition(ResourceLocation speciesID) {
        this(speciesID, false);
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(speciesID);
        buf.writeBoolean(acceptAllSpecies);
    }
    
    public static SpeciesTypeCondition decode(RegistryFriendlyByteBuf buf) {
        return fromNetwork(buf);
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(speciesID);
        buf.writeBoolean(acceptAllSpecies);
    }
    
    public static SpeciesTypeCondition fromNetwork(FriendlyByteBuf buf) {
        var id = buf.readResourceLocation();
        var acceptAll = buf.readBoolean();
        return new SpeciesTypeCondition(id, acceptAll);
    }
}
