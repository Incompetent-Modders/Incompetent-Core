package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.DefaultManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenConditionType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.DefaultSpeciesBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModManaRegenConditions {
    public static final DeferredRegister<ManaRegenConditionType<?>> MANA_REGEN_CONDITION_TYPES = DeferredRegister.create(ModRegistries.MANA_REGEN_CONDITION_TYPE, MODID);
    public static final DeferredHolder<ManaRegenConditionType<?>, ManaRegenConditionType<DefaultManaRegenCondition>> DEFAULT = MANA_REGEN_CONDITION_TYPES.register("default", () -> new ManaRegenConditionType<>(DefaultManaRegenCondition.CODEC));
    public static void register(IEventBus eventBus) {
        MANA_REGEN_CONDITION_TYPES.register(eventBus);
    }
}
