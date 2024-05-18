package com.incompetent_modders.incomp_core.api.json.spell;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = IncompCore.LOGGER;
    
    public static Map<ResourceLocation, SpellProperties> properties = new HashMap<>();
    public static List<ResourceLocation> spells = new ArrayList<>();
    public Map<ResourceLocation, SpellProperties> byName = ImmutableMap.of();
    
    public SpellListener() {
        super(GSON, "spells");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        properties.clear();
        spells.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                ResourceLocation spell = getSpellId(resourceLocation);
                SpellProperties spellProperties = SpellProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (spellProperties != null) {
                    properties.put(spell, spellProperties);
                    spells.add(spell);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading spells {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} spells", spells.size());
    }
    protected static ResourceLocation getSpellId(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", ""));
    }
    public static SpellProperties getSpellProperties(ResourceLocation spell) {
        return properties.get(spell);
    }
    
    public static List<SpellProperties> getAllSpellProperties() {
        List<SpellProperties> properties = new ArrayList<>();
        for (Map.Entry<ResourceLocation, SpellProperties> entry : SpellListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<ResourceLocation, SpellProperties> properties) {
        SpellListener.properties = properties;
    }
    
    public static boolean spellHasProperties(ResourceLocation spell) {
        return properties.containsKey(spell);
    }
    
    
    public static List<ResourceLocation> getAllSpells() {
        List<ResourceLocation> spells = new ArrayList<>();
        for (Map.Entry<ResourceLocation, SpellProperties> entry : properties.entrySet()) {
            spells.add(entry.getKey());
        }
        return spells;
    }
    
    public static void setSpells(List<ResourceLocation> spells) {
        SpellListener.spells = spells;
    }
    
    public static boolean spellExists(ResourceLocation spell) {
        return properties.containsKey(spell);
    }
    
    public static Component getDisplayName(ResourceLocation spell) {
        return Component.translatable("spells." + spell.getNamespace() + "." + spell.getPath());
    }
}
