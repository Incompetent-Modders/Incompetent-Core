package com.incompetent_modders.incomp_core.core.events;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class RegistryEvents {

    @SubscribeEvent
    public void newDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.Keys.SPELL, Spell.DIRECT_CODEC, Spell.NETWORK_CODEC);
        event.dataPackRegistry(ModRegistries.Keys.DIET, Diet.DIRECT_CODEC, Diet.NETWORK_CODEC);
        event.dataPackRegistry(ModRegistries.Keys.SPECIES_TYPE, SpeciesType.DIRECT_CODEC, SpeciesType.NETWORK_CODEC);
        event.dataPackRegistry(ModRegistries.Keys.CLASS_TYPE, ClassType.DIRECT_CODEC, ClassType.NETWORK_CODEC);
        event.dataPackRegistry(ModRegistries.Keys.POTION_PROPERTY, PotionProperty.DIRECT_CODEC, PotionProperty.NETWORK_CODEC);
    }
}
