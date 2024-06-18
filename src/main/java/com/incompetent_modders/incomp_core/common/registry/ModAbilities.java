package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.common.registry.content.abilities.ApplyEffectAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.DefaultAbility;
import com.incompetent_modders.incomp_core.common.registry.content.abilities.FunctionChargesAbility;
import com.incompetent_modders.incomp_core.common.registry.content.abilities.RunFunctionAbility;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModAbilities {
    public static final DeferredRegister<AbilityType<?>> ABILITY_TYPES = DeferredRegister.create(ModRegistries.ABILITY_TYPE, MODID);
    
    public static final DeferredHolder<AbilityType<?>, AbilityType<DefaultAbility>> DEFAULT = ABILITY_TYPES.register("default", () -> new AbilityType<>(DefaultAbility.CODEC));
    
    public static final DeferredHolder<AbilityType<?>, AbilityType<ApplyEffectAbility>> APPLY_EFFECT = ABILITY_TYPES.register("apply_effect", () -> new AbilityType<>(ApplyEffectAbility.CODEC));
    public static final DeferredHolder<AbilityType<?>, AbilityType<RunFunctionAbility>> RUN_FUNCTION = ABILITY_TYPES.register("run_function", () -> new AbilityType<>(RunFunctionAbility.CODEC));
    public static final DeferredHolder<AbilityType<?>, AbilityType<FunctionChargesAbility>> FUNCTION_CHARGES = ABILITY_TYPES.register("function_charges", () -> new AbilityType<>(FunctionChargesAbility.CODEC));
    
    public static void register(IEventBus eventBus) {
        ABILITY_TYPES.register(eventBus);
    }
}
