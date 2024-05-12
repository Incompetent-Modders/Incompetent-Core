package com.incompetent_modders.incomp_core.data.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.incompetent_modders.incomp_core.api.json.spell.SpellCategory;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.api.json.spell.SpellResults;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SpellPropertiesProvider implements DataProvider {
    private final String modid;
    private final PackOutput packOutput;
    public static Map<Spell, SpellProperties> spellProperties = new HashMap<>();
    
    public SpellPropertiesProvider(PackOutput packOutput, String modid) {
        this.modid = modid;
        this.packOutput = packOutput;
    }
    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        Set<CompletableFuture<?>> builder = new HashSet<>();
        spellProperties.forEach((spell, spellProperties) -> {
            builder.add(
                    DataProvider.saveStable(
                            pOutput,
                            this.toJson(spell, spellProperties.manaCost(), spellProperties.drawTime(), spellProperties.catalyst(), spellProperties.classType(), spellProperties.speciesType()),
                            this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(modid).resolve("spell_properties").resolve("%s.json".formatted(spell.getSpellIdentifier().getPath())))
            );
        });
        
        return CompletableFuture.allOf(
                builder.toArray(CompletableFuture[]::new)
        );
    }
    
    private JsonObject toJson(Spell spell, double manaCost, int drawTime, ItemStack catalyst, ClassType.Value classType, SpeciesType.Value speciesType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("spell", spell.getSpellIdentifier().toString());
        JsonObject properties = new JsonObject();
        properties.addProperty("mana_cost", manaCost);
        properties.addProperty("draw_time", drawTime);
        jsonObject.add("properties", properties);
        JsonObject catalystObject = new JsonObject();
        catalystObject.add("catalyst", serializeCatalyst(catalyst));
        jsonObject.add("catalyst", catalystObject);
        JsonObject classtype = new JsonObject();
        classtype.addProperty("type", classType.toString());
        jsonObject.add("class_type", classtype);
        JsonObject speciestype = new JsonObject();
        speciestype.addProperty("type", speciesType.toString());
        jsonObject.add("species_type", speciestype);
        
        return jsonObject;
    }
    public JsonElement serializeCatalyst(ItemStack catalyst) {
        JsonObject json = new JsonObject();
        ResourceLocation resourceLocation = getKeyOrThrow(catalyst.getItem());
        json.addProperty("item", resourceLocation.toString());
        int count = catalyst.getCount();
        if (count != 1)
            json.addProperty("count", count);
        if (!catalyst.getComponents().isEmpty())
            json.add("components", JsonParser.parseString(catalyst.getComponents().toString()));
        return json;
    }
    @NotNull
    public static ResourceLocation getKeyOrThrow(Item value) {
        return getKeyOrThrow(BuiltInRegistries.ITEM, value);
    }
    
    @NotNull
    public static <V> ResourceLocation getKeyOrThrow(Registry<V> registry, V value) {
        ResourceLocation key = registry.getKey(value);
        if (key == null) {
            throw new IllegalArgumentException("Could not get key for value " + value + "!");
        }
        return key;
    }
    @Override
    public String getName() {
        return "Spell Properties: " + modid;
    }
    
    public static void addSpell(Spell spell, SpellProperties properties) {
        spellProperties.put(spell, properties);
    }
    
    public static void addSpell(Spell spell, SpellCategory category, int manaCost, int drawTime, ItemStack catalyst, ClassType.Value classType, SpeciesType.Value speciesType, SpellResults results, SoundEvent soundEvent) {
        spellProperties.put(spell, new SpellProperties(category, manaCost, drawTime, catalyst, classType, speciesType, results, soundEvent));
    }
    
    public void provideSpellProperties() {
        // Override this method to add spells
    }
}
