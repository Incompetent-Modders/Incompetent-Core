package com.incompetent_modders.incomp_core.client;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientClassTypeManager {
    private static ClientClassTypeManager instance;
    private List<ResourceLocation> classTypeList;
    
    private ClientClassTypeManager() {
        classTypeList = new ArrayList<>();
    }
    
    public static ClientClassTypeManager getInstance() {
        if (instance == null) {
            instance = new ClientClassTypeManager();
        }
        return instance;
    }
    
    public List<ResourceLocation> getClassTypeList() {
        return classTypeList;
    }
    
    public void updateClassTypeList(List<ResourceLocation> updatedClassTypes) {
        classTypeList.clear();
        classTypeList.addAll(updatedClassTypes);
    }
}
