package com.incompetent_modders.incomp_core.core.events;

import com.incompetent_modders.incomp_core.core.events.forge.EntityEvents;
import com.incompetent_modders.incomp_core.core.events.forge.ItemEvents;
import com.incompetent_modders.incomp_core.core.events.forge.PlayerEvents;
import com.incompetent_modders.incomp_core.core.events.mod.CapabilityEvents;
import com.incompetent_modders.incomp_core.core.events.mod.NetworkEvents;
import com.incompetent_modders.incomp_core.core.events.mod.RegistryEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class IncompetentEventHandler {
    private final IEventBus modEventBus;

    public IncompetentEventHandler(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    public void registerModEvents(IEventBus eventBus) {
        eventBus.register(new RegistryEvents());
        eventBus.register(new CapabilityEvents());
        eventBus.register(new NetworkEvents());
    }

    public void registerForgeEvents(IEventBus eventBus) {
        eventBus.register(new PlayerEvents());
        eventBus.register(new EntityEvents());
        eventBus.register(new ItemEvents());
    }

    public void register() {
        registerModEvents(modEventBus);
        registerForgeEvents(NeoForge.EVENT_BUS);
    }
}
