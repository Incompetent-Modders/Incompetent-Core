package com.incompetent_modders.incomp_core.client.managers;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ClientSpellManager {
    private static ClientSpellManager instance;
    private static final Map<ResourceLocation, SpellProperties> spellProperties = new HashMap<>();
    
    private ClientSpellManager() {
        // Initialize the spell list (e.g., load from packet data)
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client spell list");
    }
    
    public static ClientSpellManager getInstance() {
        if (instance == null) {
            instance = new ClientSpellManager();
        }
        return instance;
    }
    
    public Map<ResourceLocation, SpellProperties> getSpellList() {
        return spellProperties;
    }
    
    public void updateSpellList(Map<ResourceLocation, SpellProperties> updatedSpells) {
        spellProperties.clear();
        spellProperties.putAll(updatedSpells);
        IncompCore.LOGGER.info("[CLIENT ONLY] Updated spell list with {} spells", spellProperties.size());
    }
    
    public static Component getDisplayName(ResourceLocation spell) {
        return Component.translatable("spells." + spell.getNamespace() + "." + spell.getPath().replace("/", "."));
    }
    
    public static SpellProperties getSpellProperties(ResourceLocation spell) {
        return spellProperties.get(spell);
    }
    
}
