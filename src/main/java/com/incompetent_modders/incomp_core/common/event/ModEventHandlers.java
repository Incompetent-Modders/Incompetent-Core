package com.incompetent_modders.incomp_core.common.event;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class ModEventHandlers {
    private final IEventBus modEventBus;

    public ModEventHandlers(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    public void registerModEvents(IEventBus eventBus) {
        eventBus.register(new RegistryEvents());
    }

    public void registerForgeEvents(IEventBus eventBus) {

    }

    public void register() {
        registerModEvents(modEventBus);
        registerForgeEvents(NeoForge.EVENT_BUS);
    }
}
