package com.incompetent_modders.incomp_core.api.json.spell;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SpellPropertyListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = IncompCore.LOGGER;
    
    public static Map<Spell, SpellProperties> properties = new HashMap<>();
    public Map<ResourceLocation, Spell> byName = ImmutableMap.of();
    
    public SpellPropertyListener() {
        super(GSON, "spell_properties");
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
                Spell spell = getSpell(resourceLocation);
                SpellProperties spellProperties = SpellProperties.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                if (spellProperties != null) {
                    properties.put(spell, spellProperties);
                }
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                IncompCore.LOGGER.error("Parsing error loading spell properties {}", resourceLocation, jsonParseException);
            }
        }
        IncompCore.LOGGER.info("Load Complete for {} spells", properties.size());
    }
    protected static Spell getSpell(ResourceLocation resourceLocation) {
        return ModRegistries.SPELL.get(new ResourceLocation(resourceLocation.getNamespace(), CommonUtils.removeExtension(resourceLocation).replace(".json", "")));
    }
    
    public static SpellProperties getSpellProperties(Spell spell) {
        return properties.get(spell);
    }
    public static List<SpellProperties> getAllSpellProperties() {
        List<SpellProperties> properties = new ArrayList<>();
        for (Map.Entry<Spell, SpellProperties> entry : SpellPropertyListener.properties.entrySet()) {
            properties.add(entry.getValue());
        }
        return properties;
    }
    
    public static void setProperties(Map<Spell, SpellProperties> properties) {
        SpellPropertyListener.properties = properties;
    }
    
    public static boolean spellHasProperties(Spell spell) {
        return properties.containsKey(spell);
    }
}
