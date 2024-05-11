package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.DefaultClassAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.DefaultClassPassiveEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModClassAbilities {
    public static final DeferredRegister<ClassAbilityType<?>> CLASS_ABILITY_TYPES = DeferredRegister.create(ModRegistries.CLASS_ABILITY_TYPE, MODID);
    public static final DeferredHolder<ClassAbilityType<?>, ClassAbilityType<DefaultClassAbility>> DEFAULT = CLASS_ABILITY_TYPES.register("default", () -> new ClassAbilityType<>(DefaultClassAbility.CODEC));
    public static void register(IEventBus eventBus) {
        CLASS_ABILITY_TYPES.register(eventBus);
    }
}
