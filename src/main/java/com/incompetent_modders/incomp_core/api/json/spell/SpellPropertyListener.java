package com.incompetent_modders.incomp_core.api.json.spell;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
@ParametersAreNonnullByDefault
public class SpellPropertyListener extends SimpleJsonResourceReloadListener {
    public static final SpellPropertyListener instance = new SpellPropertyListener();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = IncompCore.LOGGER;
    public static Map<Spell, Map<ResourceLocation, Spell>> spells = ImmutableMap.of();
    public Map<ResourceLocation, Spell> byName = ImmutableMap.of();
    public static Map<Spell, Integer> manaCosts = ImmutableMap.of();
    public static Map<Spell, Integer> drawTimes = ImmutableMap.of();
    public static Map<Spell, Integer> cooldowns = ImmutableMap.of();
    public static Map<Spell, ItemStack> catalysts = ImmutableMap.of();
    
    public SpellPropertyListener() {
        super(GSON, "spell_properties");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Spell, ImmutableMap.Builder<ResourceLocation, Spell>> map = Maps.newHashMap();
        ImmutableMap.Builder<ResourceLocation, Spell> builder = ImmutableMap.builder();
        
        for(Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            try {
                Spell spell = getSpell(entry.getValue());
                LOGGER.info("%s ||| %s".formatted(entry.getKey(), entry.getValue()));
                builder.put(entry.getKey(), spell);
                manaCosts.put(spell, entry.getValue().getAsJsonObject().get("properties").getAsJsonObject().get("mana").getAsInt());
                drawTimes.put(spell, entry.getValue().getAsJsonObject().get("properties").getAsJsonObject().get("draw_time").getAsInt());
                cooldowns.put(spell, entry.getValue().getAsJsonObject().get("properties").getAsJsonObject().get("cooldown").getAsInt());
                catalysts.put(spell, deserializeCatalyst(entry.getValue()));
                map.computeIfAbsent(spell, s -> ImmutableMap.builder()).put(entry.getKey(), spell);
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                LOGGER.error("Parsing error loading spell properties {}", entry.getKey(), jsonParseException);
            }
        }
        
        spells = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> e.getValue().build()));
        this.byName = builder.build();
        LOGGER.info("Loaded Properties for {} spells", spells.size());
    }
    
    protected Spell getSpell(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        return ModRegistries.SPELL.get(new ResourceLocation(json.get("spell").getAsString()));
    }
    protected ItemStack deserializeCatalyst(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        JsonObject catalyst = json.get("catalyst").getAsJsonObject();
        String itemId = GsonHelper.getAsString(catalyst, "item");
        int count = GsonHelper.getAsInt(catalyst, "count", 1);
        ItemStack itemstack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(itemId)), count);
        if (GsonHelper.isValidNode(json, "custom_data")) {
            try {
                JsonElement element = json.get("custom_data");
                itemstack.setTag(TagParser.parseTag(
                        element.isJsonObject() ? GSON.toJson(element) : GsonHelper.convertToString(element, "nbt")));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return itemstack;
    }
}
