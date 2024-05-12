package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellPropertyListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class CommonEventHandler {
    @SubscribeEvent
    public void jsonReading(AddReloadListenerEvent event) {
        SpellPropertyListener propertyListener = new SpellPropertyListener();
        PotionEffectPropertyListener potionEffectListener = new PotionEffectPropertyListener();
        ClassTypeListener classTypeListener = new ClassTypeListener();
        SpeciesListener speciesListener = new SpeciesListener();
        SpeciesAttributesListener speciesAttributesListener = new SpeciesAttributesListener();
        event.addListener(propertyListener);
        event.addListener(potionEffectListener);
        event.addListener(classTypeListener);
        event.addListener(speciesListener);
        event.addListener(speciesAttributesListener);
    }
    
}