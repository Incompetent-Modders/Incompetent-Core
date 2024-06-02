package com.incompetent_modders.incomp_core.client;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientSpellManager {
    private static ClientSpellManager instance;
    private List<ResourceLocation> spellList;
    
    private ClientSpellManager() {
        // Initialize the spell list (e.g., load from packet data)
        spellList = new ArrayList<>();
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client spell list");
    }
    
    public static ClientSpellManager getInstance() {
        if (instance == null) {
            instance = new ClientSpellManager();
        }
        return instance;
    }
    
    public List<ResourceLocation> getSpellList() {
        return spellList;
    }
    
    public void updateSpellList(List<ResourceLocation> updatedSpells) {
        spellList.clear();
        spellList.addAll(updatedSpells);
        IncompCore.LOGGER.info("[CLIENT ONLY] Updated spell list with {} spells", spellList.size());
    }
}
