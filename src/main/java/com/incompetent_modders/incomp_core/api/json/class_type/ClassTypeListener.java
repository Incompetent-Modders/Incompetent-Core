package com.incompetent_modders.incomp_core.api.json.class_type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
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

public class ClassTypeListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    private static final Logger LOGGER = IncompCore.LOGGER;
    public static Map<ClassType, ClassTypeProperties> properties = new HashMap<>();
    public ClassTypeListener() {
        super(GSON, "class_definitions");
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
                ClassType classType = getClassType(resourceLocation);
                ClassTypeProperties classTypeProperties = ClassTypeProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (classTypeProperties != null) {
                    properties.put(classType, classTypeProperties);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading class type properties {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} class types", properties.size());
    }
    
    protected static ClassType getClassType(ResourceLocation resourceLocation) {
        return ModRegistries.CLASS_TYPE.get(new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", "")));
    }
    
    public static ClassTypeProperties getClassTypeProperties(ClassType classType) {
        return properties.get(classType);
    }
    
    public static List<ClassTypeProperties> getAllClassTypeProperties() {
        List<ClassTypeProperties> properties = new ArrayList<>();
        for (Map.Entry<ClassType, ClassTypeProperties> entry : ClassTypeListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<ClassType, ClassTypeProperties> properties) {
        ClassTypeListener.properties = properties;
    }
    
    public static boolean classTypeHasProperties(ClassType classType) {
        return properties.containsKey(classType);
    }
}
