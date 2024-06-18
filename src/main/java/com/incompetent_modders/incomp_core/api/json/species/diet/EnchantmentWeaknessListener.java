package com.incompetent_modders.incomp_core.api.json.species.diet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.incompetent_modders.incomp_core.IncompCore;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentWeaknessListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    private static final Logger LOGGER = IncompCore.LOGGER;
    public static Map<ResourceLocation, EnchantmentWeaknessProperties> enchantmentWeaknesses = new HashMap<>();
    
    public EnchantmentWeaknessListener() {
        super(GSON, "species_definitions/enchantment_weaknesses");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        enchantmentWeaknesses.clear();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            
            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }
            
            try {
                ResourceLocation weakness = getWeaknessId(resourceLocation);
                EnchantmentWeaknessProperties enchantmentWeakness = EnchantmentWeaknessProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (enchantmentWeakness != null) {
                    enchantmentWeaknesses.put(weakness, enchantmentWeakness);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading species enchantment weakness {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} species' enchantment weaknesses", enchantmentWeaknesses.size());
    }
    
    protected static ResourceLocation getWeaknessId(ResourceLocation resourceLocation) {
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), resourceLocation.getPath().substring(0, resourceLocation.getPath().length() - 5));
    }
}
