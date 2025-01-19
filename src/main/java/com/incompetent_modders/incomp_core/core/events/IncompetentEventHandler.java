package com.incompetent_modders.incomp_core.core.events;

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

    }

    public void register() {
        registerModEvents(modEventBus);
        registerForgeEvents(NeoForge.EVENT_BUS);
    }
}
