package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.UndeadSpeciesBehaviour;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class ModSpeciesTypes {
    public static final ResourceKey<SpeciesType> ZOMBIE = create("zombie");

    private static ResourceKey<SpeciesType> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.SPECIES_TYPE, IncompCore.makeId(name));
    }

    private static void registerSpecies(BootstrapContext<SpeciesType> context) {
        register(context, ZOMBIE, SpeciesType.builder().behaviour(new UndeadSpeciesBehaviour(true)).dietType(ModDiets.CARNIVORE.location()).invertPotionHealAndHarm().keepOnDeath(true));
    }

    private static void register(BootstrapContext<SpeciesType> context, ResourceKey<SpeciesType> key, SpeciesType.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<SpeciesType> context) {
        registerSpecies(context);
    }
}
