package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class IncompSpeciesTags {
    
    public static TagKey<SpeciesType> undead = modSpeciesTag("undead");
    
    public static TagKey<SpeciesType> smiteVulnerable = modSpeciesTag("smite_vulnerable");
    public static TagKey<SpeciesType> arthropod = modSpeciesTag("arthropod");
    public static TagKey<SpeciesType> impalingVulnerable = modSpeciesTag("impaling_vulnerable");
    public static TagKey<SpeciesType> ignoresHungerFromFood = modSpeciesTag("ignores_hunger_from_food");
    
    public static TagKey<SpeciesType> modSpeciesTag(String path) {
        return create(new ResourceLocation(IncompCore.MODID, path));
    }
    public static TagKey<SpeciesType> modSpeciesTag(String namespace, String path) {
        return create(new ResourceLocation(namespace, path));
    }
    public static TagKey<SpeciesType> forgeSpeciesTag(String path) {
        return create(new ResourceLocation("forge", path));
    }
    
    public static TagKey<SpeciesType> create(ResourceLocation name) {
        return TagKey.create(ModRegistries.SPECIES_TYPE_KEY, name);
    }
}
