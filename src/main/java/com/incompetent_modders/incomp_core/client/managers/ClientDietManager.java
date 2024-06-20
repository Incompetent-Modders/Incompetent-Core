package com.incompetent_modders.incomp_core.client.managers;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientDietManager {
    private static ClientDietManager instance;
    private List<ResourceLocation> dietList;
    
    private ClientDietManager() {
        dietList = new ArrayList<>();
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client diet list");
    }
    
    public static ClientDietManager getInstance() {
        if (instance == null) {
            instance = new ClientDietManager();
        }
        return instance;
    }
    
    public List<ResourceLocation> getDietList() {
        return dietList;
    }
    
    public void updateDietList(List<ResourceLocation> updatedDiets) {
        dietList.clear();
        dietList.addAll(updatedDiets);
        IncompCore.LOGGER.info("[CLIENT ONLY] Updated diet list with {} diets", updatedDiets.size());
    }
}
