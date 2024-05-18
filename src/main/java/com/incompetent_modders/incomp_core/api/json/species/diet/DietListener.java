package com.incompetent_modders.incomp_core.api.json.species.diet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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

public class DietListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    private static final Logger LOGGER = IncompCore.LOGGER;
    
    public static Map<ResourceLocation, DietProperties> properties = new HashMap<>();
    public static List<ResourceLocation> diets = new ArrayList<>();
    
    public DietListener() {
        super(GSON, "species_definitions/diet");
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        properties.clear();
        diets.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                ResourceLocation dietType = getDietId(resourceLocation);
                DietProperties dietProperties = DietProperties.CODEC.codec().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (dietProperties != null) {
                    properties.put(dietType, dietProperties);
                    diets.add(dietType);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading diet {}", resourceLocation, jsonParseException);
            }
        }
        
        IncompCore.LOGGER.info("Load Complete for {} diets", diets.size());
    }
    
    public static ResourceLocation getDietId(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", ""));
    }
    
    public static DietProperties getDietProperties(ResourceLocation resourceLocation) {
        return properties.get(resourceLocation);
    }
    public static Component getDisplayName(ResourceLocation resourceLocation) {
        return Component.translatable("diets." + resourceLocation.getNamespace() + "." + resourceLocation.getPath());
    }
    public static List<ResourceLocation> getAllDiets() {
        List<ResourceLocation> diets = new ArrayList<>();
        for (Map.Entry<ResourceLocation, DietProperties> entry : properties.entrySet()) {
            diets.add(entry.getKey());
        }
        return diets;
    }
    
    public static List<DietProperties> getAllDietProperties() {
        List<DietProperties> properties = new ArrayList<>();
        for (Map.Entry<ResourceLocation, DietProperties> entry : DietListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<ResourceLocation, DietProperties> properties) {
        DietListener.properties = properties;
    }
    
    public static void setDiet(List<ResourceLocation> diet) {
        DietListener.diets = diet;
    }
    
    
}
