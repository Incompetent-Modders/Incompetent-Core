package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.DefaultClassPassiveEffect;
import com.incompetent_modders.incomp_core.common.registry.content.passive_effects.ApplyEffectPassiveEffect;
import com.incompetent_modders.incomp_core.common.registry.content.passive_effects.RunFunctionPassiveEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModClassPassiveEffects {
    public static final DeferredRegister<ClassPassiveEffectType<?>> CLASS_PASSIVE_EFFECT_TYPES = DeferredRegister.create(ModRegistries.CLASS_PASSIVE_EFFECT_TYPE, MODID);
    
    public static final DeferredHolder<ClassPassiveEffectType<?>, ClassPassiveEffectType<DefaultClassPassiveEffect>> DEFAULT = CLASS_PASSIVE_EFFECT_TYPES.register("default", () -> new ClassPassiveEffectType<>(DefaultClassPassiveEffect.CODEC));
    
    public static final DeferredHolder<ClassPassiveEffectType<?>, ClassPassiveEffectType<ApplyEffectPassiveEffect>> APPLY_EFFECT = CLASS_PASSIVE_EFFECT_TYPES.register("apply_effect", () -> new ClassPassiveEffectType<>(ApplyEffectPassiveEffect.CODEC));
    public static final DeferredHolder<ClassPassiveEffectType<?>, ClassPassiveEffectType<RunFunctionPassiveEffect>> RUN_FUNCTION = CLASS_PASSIVE_EFFECT_TYPES.register("run_function", () -> new ClassPassiveEffectType<>(RunFunctionPassiveEffect.CODEC));
    
    public static void register(IEventBus eventBus) {
        CLASS_PASSIVE_EFFECT_TYPES.register(eventBus);
    }
}
