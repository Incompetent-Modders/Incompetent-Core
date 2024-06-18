package com.incompetent_modders.incomp_core.common.event;

import java.util.function.Consumer;

public record SetupEvent(Consumer<Runnable> enqueue) {
    
    public static final EventHandler<SetupEvent> EVENT = new EventHandler<>();
    
    public void enqueueWork(Runnable runnable) {
        enqueue.accept(runnable);
    }
}
