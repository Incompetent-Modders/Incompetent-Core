package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModSpeciesTypes {
    public static final DeferredRegister<SpeciesType> SPECIES_TYPES = DeferredRegister.create(ModRegistries.SPECIES_TYPE, MODID);
    
    public static final DeferredHolder<SpeciesType, SpeciesType> HUMAN = SPECIES_TYPES.register("human", SpeciesType::new);
    public static final DeferredHolder<SpeciesType, SpeciesType> ZOMBIE = SPECIES_TYPES.register("zombie", SpeciesType::new);
    public static void register(IEventBus eventBus) {
        SPECIES_TYPES.register(eventBus);
    }
}
