package com.incompetent_modders.incomp_core.api.json.spell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

//Allows for potion effects to give certain effects to how the player interacts with spells.
public class PotionEffectPropertyListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    private static final Logger LOGGER = IncompCore.LOGGER;
    public static Map<MobEffect, PotionEffectProperties> properties = new HashMap<>();
    public PotionEffectPropertyListener() {
        super(GSON, "potion_effect_properties");
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
                MobEffect effect = getEffect(resourceLocation);
                if (effect == null) {
                    LOGGER.error("Invalid potion effect: {}", resourceLocation);
                    continue;
                }
                PotionEffectProperties potionEffectProperties = PotionEffectProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (potionEffectProperties != null) {
                    properties.put(effect, potionEffectProperties);
                }
            } catch (IllegalArgumentException | com.google.gson.JsonParseException jsonParseException) {
                LOGGER.error("Parsing error loading potion effect properties input {}", resourceLocation, jsonParseException);
            }
        }
        LOGGER.info("Load Complete for {} potion effects", properties.size());
    }
    
    protected static MobEffect getEffect(ResourceLocation resourceLocation) {
        return BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", "")));
    }
    
    public static PotionEffectProperties getEffectProperties(MobEffect effect) {
        return properties.get(effect);
    }
}
