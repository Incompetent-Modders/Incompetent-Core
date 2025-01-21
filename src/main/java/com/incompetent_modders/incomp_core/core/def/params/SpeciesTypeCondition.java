package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;

public record SpeciesTypeCondition(ResourceKey<SpeciesType> speciesKey, boolean acceptAllSpecies) {
    public static final Codec<SpeciesTypeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(ModRegistries.Keys.SPECIES_TYPE).optionalFieldOf("species", ModSpeciesTypes.HUMAN).forGetter(SpeciesTypeCondition::speciesKey),
            Codec.BOOL.optionalFieldOf("accept_all_species", false).forGetter(SpeciesTypeCondition::acceptAllSpecies)
    ).apply(instance, SpeciesTypeCondition::new));
    
    public static final SpeciesTypeCondition ANY = new SpeciesTypeCondition(ModSpeciesTypes.HUMAN, true);
    
    public SpeciesTypeCondition(ResourceKey<SpeciesType> speciesID) {
        this(speciesID, false);
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceKey(speciesKey);
        buf.writeBoolean(acceptAllSpecies);
    }
    
    public static SpeciesTypeCondition decode(RegistryFriendlyByteBuf buf) {
        return fromNetwork(buf);
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceKey(speciesKey);
        buf.writeBoolean(acceptAllSpecies);
    }
    
    public static SpeciesTypeCondition fromNetwork(FriendlyByteBuf buf) {
        var key = buf.readResourceKey(ModRegistries.Keys.SPECIES_TYPE);
        var acceptAll = buf.readBoolean();
        return new SpeciesTypeCondition(key, acceptAll);
    }
}
