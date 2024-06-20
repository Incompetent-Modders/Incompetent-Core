package com.incompetent_modders.incomp_core.client.managers;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientSpeciesManager {
    private static ClientSpeciesManager instance;
    private List<ResourceLocation> speciesList;
    
    private ClientSpeciesManager() {
        speciesList = new ArrayList<>();
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client species list");
    }
    
    public static ClientSpeciesManager getInstance() {
        if (instance == null) {
            instance = new ClientSpeciesManager();
        }
        return instance;
    }
    
    public List<ResourceLocation> getSpeciesList() {
        return speciesList;
    }
    
    public void updateSpeciesList(List<ResourceLocation> updatedSpecies) {
        speciesList.clear();
        speciesList.addAll(updatedSpecies);
        IncompCore.LOGGER.info("[CLIENT ONLY] Updated species list with {} species", speciesList.size());
    }
}
