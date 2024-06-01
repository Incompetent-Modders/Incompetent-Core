package com.incompetent_modders.incomp_core.api.json.species;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.common.util.Utils;
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

public class SpeciesListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    private static final Logger LOGGER = IncompCore.LOGGER;
    public static Map<ResourceLocation, SpeciesProperties> properties = new HashMap<>();
    public static List<ResourceLocation> species = new ArrayList<>();
    
    public SpeciesListener() {
        super(GSON, "species_definitions/properties");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        properties.clear();
        species.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                ResourceLocation speciesType = getSpeciesId(resourceLocation);
                SpeciesProperties speciesProperties = SpeciesProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (speciesProperties != null) {
                    properties.put(speciesType, speciesProperties);
                    species.add(speciesType);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading species properties {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} species", species.size());
    }
    
    protected static ResourceLocation getSpeciesId(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), Utils.removeExtension(resourceLocation).replace(".json", ""));
    }
    public static SpeciesProperties getSpeciesTypeProperties(ResourceLocation speciesType) {
        return properties.get(speciesType);
    }
    
    public static List<SpeciesProperties> getAllSpeciesTypeProperties() {
        List<SpeciesProperties> properties = new ArrayList<>();
        for (Map.Entry<ResourceLocation, SpeciesProperties> entry : SpeciesListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<ResourceLocation, SpeciesProperties> properties) {
        SpeciesListener.properties = properties;
    }
    
    public static List<ResourceLocation> getAllSpecies() {
        List<ResourceLocation> species = new ArrayList<>();
        for (Map.Entry<ResourceLocation, SpeciesProperties> entry : properties.entrySet()) {
            species.add(entry.getKey());
        }
        return species;
    }
    
    public static boolean speciesHasProperties(ResourceLocation species) {
        return properties.containsKey(species);
    }
    
    public static void setSpecies(List<ResourceLocation> species) {
        SpeciesListener.species = species;
    }
    
    public static boolean speciesExists(ResourceLocation species) {
        return properties.containsKey(species);
    }
    
    public static Component getDisplayName(ResourceLocation species) {
        return Component.translatable("species." + species.getNamespace() + "." + species.getPath());
    }
}
