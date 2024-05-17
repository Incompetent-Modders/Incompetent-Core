package com.incompetent_modders.incomp_core.command.arguments;

import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.function.Predicate;

public class SpeciesInput {
    private final Holder<SpeciesType> speciesType;
    
    public SpeciesInput(Holder<SpeciesType> speciesType) {
        this.speciesType = speciesType;
    }
    public SpeciesType getSpeciesType() {
        return this.speciesType.value();
    }
    
    public SpeciesType createSpecies() throws CommandSyntaxException {
        return this.getSpeciesType();
    }
    
    public String serialize() {
        return this.getSpeciesName();
    }
    
    
    private String getSpeciesName() {
        return this.speciesType.unwrapKey().<Object>map(ResourceKey::location).orElseGet(() -> {
            return "unknown[" + String.valueOf(this.speciesType) + "]";
        }).toString();
    }
}
