package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.DefaultSpeciesBehaviour;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.UndeadSpeciesBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModSpeciesBehaviourTypes {
    public static final DeferredRegister<SpeciesBehaviourType<?>> SPECIES_BEHAVIOUR_TYPES = DeferredRegister.create(ModRegistries.SPECIES_BEHAVIOUR_TYPE, MODID);
    
    public static final DeferredHolder<SpeciesBehaviourType<?>, SpeciesBehaviourType<DefaultSpeciesBehaviour>> DEFAULT = SPECIES_BEHAVIOUR_TYPES.register("default", () -> new SpeciesBehaviourType<>(DefaultSpeciesBehaviour.CODEC));
    public static final DeferredHolder<SpeciesBehaviourType<?>, SpeciesBehaviourType<UndeadSpeciesBehaviour>> UNDEAD = SPECIES_BEHAVIOUR_TYPES.register("undead", () -> new SpeciesBehaviourType<>(UndeadSpeciesBehaviour.CODEC));
    public static void register(IEventBus eventBus) {
        SPECIES_BEHAVIOUR_TYPES.register(eventBus);
    }
}
