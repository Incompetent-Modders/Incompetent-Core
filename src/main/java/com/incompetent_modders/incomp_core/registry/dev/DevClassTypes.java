package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class DevClassTypes {
    public static final DeferredRegister<ClassType> CLASS_TYPES = DeferredRegister.create(ModRegistries.CLASS_TYPE, MODID);
    
    public static final DeferredHolder<ClassType, ClassType> TEST_CLASS = CLASS_TYPES.register("test_class", () -> new ClassType(true, 150, true, 0));
    public static void register(IEventBus eventBus) {
        CLASS_TYPES.register(eventBus);
    }
}
