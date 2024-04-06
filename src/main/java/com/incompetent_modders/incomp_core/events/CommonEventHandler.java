package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.api.json.spell.SpellPropertyListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class CommonEventHandler {
    @SubscribeEvent
    public void jsonReading(AddReloadListenerEvent event) {
        SpellPropertyListener propertyListener = new SpellPropertyListener();
        event.addListener(propertyListener);
    }
}
