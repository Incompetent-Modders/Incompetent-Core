package com.incompetent_modders.incomp_core.api.json.class_type;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassTypeListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    public static Map<ResourceLocation, ClassTypeProperties> properties = new HashMap<>();
    public static List<ResourceLocation> classTypes = new ArrayList<>();
    public ClassTypeListener() {
        super(GSON, "class_definitions");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        properties.clear();
        classTypes.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                ResourceLocation classType = getClassTypeID(resourceLocation);
                ClassTypeProperties classTypeProperties = ClassTypeProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (classTypeProperties != null) {
                    properties.put(classType, classTypeProperties);
                    classTypes.add(classType);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading class type properties {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} class types", classTypes.size());
    }
    
    protected static ResourceLocation getClassTypeID(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), Utils.removeExtension(resourceLocation).replace(".json", ""));
    }
    
    public static ClassTypeProperties getClassTypeProperties(ResourceLocation classType) {
        return properties.get(classType);
    }
    
    public static List<ClassTypeProperties> getAllClassTypeProperties() {
        List<ClassTypeProperties> properties = new ArrayList<>();
        for (Map.Entry<ResourceLocation, ClassTypeProperties> entry : ClassTypeListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<ResourceLocation, ClassTypeProperties> properties) {
        ClassTypeListener.properties = properties;
    }
    
    public static boolean classTypeHasProperties(ResourceLocation classType) {
        return properties.containsKey(classType);
    }
    
    public static List<ResourceLocation> getAllClassTypes() {
        List<ResourceLocation> classTypes = new ArrayList<>();
        for (Map.Entry<ResourceLocation, ClassTypeProperties> entry : properties.entrySet()) {
            classTypes.add(entry.getKey());
        }
        return classTypes;
    }
    
    public static void setClassTypes(List<ResourceLocation> classTypes) {
        ClassTypeListener.classTypes = classTypes;
    }
    
    public static Component getDisplayName(ResourceLocation classType) {
        return Component.translatable("class_types." + classType.getNamespace() + "." + classType.getPath());
    }
    
    
    
    
}
