package com.incompetent_modders.incomp_core.api.json.species;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.incompetent_modders.incomp_core.IncompCore;
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

public class SpeciesAttributesListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    private static final Logger LOGGER = IncompCore.LOGGER;
    public static Map<ResourceLocation, SpeciesAttributes> attributes = new HashMap<>();
    
    public SpeciesAttributesListener() {
        super(GSON, "species_definitions/attributes");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        attributes.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                ResourceLocation speciesType = getSpeciesId(resourceLocation);
                SpeciesAttributes speciesAttributes = SpeciesAttributes.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (speciesAttributes != null) {
                    attributes.put(speciesType, speciesAttributes);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading species attributes {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} species' attributes", attributes.size());
    }
    
    protected static ResourceLocation getSpeciesId(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", ""));
    }
    
    public static SpeciesAttributes getSpeciesTypeAttributes(ResourceLocation speciesType) {
        return attributes.get(speciesType);
    }
    
    public static List<SpeciesAttributes> getAllSpeciesTypeAttributes() {
        List<SpeciesAttributes> attributes = new ArrayList<>();
        for (Map.Entry<ResourceLocation, SpeciesAttributes> entry : SpeciesAttributesListener.attributes.entrySet()) {
            attributes.add(entry.getValue());
        }
        return attributes;
    }
    
    public static void setAttributes(Map<ResourceLocation, SpeciesAttributes> attributes) {
        SpeciesAttributesListener.attributes = attributes;
    }
}
