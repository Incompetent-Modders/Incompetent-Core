package com.incompetent_modders.incomp_core.client;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientDietManager {
    private static ClientDietManager instance;
    private List<ResourceLocation> dietList;
    
    private ClientDietManager() {
        dietList = new ArrayList<>();
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
    }
}
