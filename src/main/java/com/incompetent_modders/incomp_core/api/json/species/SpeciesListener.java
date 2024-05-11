package com.incompetent_modders.incomp_core.api.json.species;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.JsonOps;
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
    public static Map<SpeciesType, SpeciesProperties> properties = new HashMap<>();
    
    public SpeciesListener() {
        super(GSON, "species_definitions/properties");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        properties.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                SpeciesType speciesType = getSpeciesType(resourceLocation);
                SpeciesProperties speciesProperties = SpeciesProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (speciesProperties != null) {
                    properties.put(speciesType, speciesProperties);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading species properties {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} species", properties.size());
    }
    
    protected static SpeciesType getSpeciesType(ResourceLocation resourceLocation) {
        return ModRegistries.SPECIES_TYPE.get(new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", "")));
    }
    
    public static SpeciesProperties getSpeciesTypeProperties(SpeciesType speciesType) {
        return properties.get(speciesType);
    }
    
    public static List<SpeciesProperties> getAllSpeciesTypeProperties() {
        List<SpeciesProperties> properties = new ArrayList<>();
        for (Map.Entry<SpeciesType, SpeciesProperties> entry : SpeciesListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<SpeciesType, SpeciesProperties> properties) {
        SpeciesListener.properties = properties;
    }
}
