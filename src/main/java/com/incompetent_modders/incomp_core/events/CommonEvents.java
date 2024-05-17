package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.command.ModCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
@EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext());
    }
    
    
}
