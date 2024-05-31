package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.potion.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class CommonEventHandler {
    @SubscribeEvent
    public void jsonReading(AddReloadListenerEvent event) {
        SpellListener propertyListener = new SpellListener();
        PotionEffectPropertyListener potionEffectListener = new PotionEffectPropertyListener();
        ClassTypeListener classTypeListener = new ClassTypeListener();
        SpeciesListener speciesListener = new SpeciesListener();
        SpeciesAttributesListener speciesAttributesListener = new SpeciesAttributesListener();
        DietListener dietListener = new DietListener();
        //EnchantmentWeaknessListener enchantmentWeaknessListener = new EnchantmentWeaknessListener();
        event.addListener(propertyListener);
        event.addListener(potionEffectListener);
        event.addListener(classTypeListener);
        event.addListener(speciesListener);
        event.addListener(speciesAttributesListener);
        event.addListener(dietListener);
        //event.addListener(enchantmentWeaknessListener);
    }
    
}
