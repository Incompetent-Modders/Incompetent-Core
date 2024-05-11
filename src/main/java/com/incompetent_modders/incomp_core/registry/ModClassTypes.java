package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModClassTypes {
    
    public static final DeferredRegister<ClassType> CLASS_TYPES = DeferredRegister.create(ModRegistries.CLASS_TYPE, MODID);
    
    public static final DeferredHolder<ClassType, ClassType> NONE = CLASS_TYPES.register("none", ClassType::new);
    
    public static void register(IEventBus eventBus) {
        CLASS_TYPES.register(eventBus);
    }
}
