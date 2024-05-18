package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenConditionType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModRegistries {
    public static final ResourceKey<Registry<ClassType>> CLASS_TYPE_KEY = createRegistryKey("class_type");
    public static final ResourceKey<Registry<SpeciesType>> SPECIES_TYPE_KEY = createRegistryKey("species_type");
    public static final ResourceKey<Registry<ClassAbilityType<?>>> CLASS_ABILITY_TYPE_KEY = createRegistryKey("class_ability_type");
    public static final ResourceKey<Registry<ClassPassiveEffectType<?>>> CLASS_PASSIVE_EFFECT_TYPE_KEY = createRegistryKey("class_passive_effect_type");
    public static final ResourceKey<Registry<ManaRegenConditionType<?>>> MANA_REGEN_CONDITION_TYPE_KEY = createRegistryKey("mana_regen_condition_type");
    public static final ResourceKey<Registry<SpeciesBehaviourType<?>>> SPECIES_BEHAVIOUR_TYPE_KEY = createRegistryKey("species_behaviour_type");
    public static final ResourceKey<Registry<SpellResultType<?>>> SPELL_RESULT_TYPES_KEY = createRegistryKey("spell_result_type");
    
    public static final Registry<ClassType> CLASS_TYPE = registerSimpleWithIntrusiveHolders(CLASS_TYPE_KEY);
    public static final Registry<SpeciesType> SPECIES_TYPE = registerSimpleWithIntrusiveHolders(SPECIES_TYPE_KEY);
    public static final Registry<ClassAbilityType<?>> CLASS_ABILITY_TYPE = makeSyncedRegistry(CLASS_ABILITY_TYPE_KEY);
    public static final Registry<ClassPassiveEffectType<?>> CLASS_PASSIVE_EFFECT_TYPE = makeSyncedRegistry(CLASS_PASSIVE_EFFECT_TYPE_KEY);
    public static final Registry<ManaRegenConditionType<?>> MANA_REGEN_CONDITION_TYPE = makeSyncedRegistry(MANA_REGEN_CONDITION_TYPE_KEY);
    public static final Registry<SpeciesBehaviourType<?>> SPECIES_BEHAVIOUR_TYPE = makeSyncedRegistry(SPECIES_BEHAVIOUR_TYPE_KEY);
    public static final Registry<SpellResultType<?>> SPELL_RESULT_TYPE = makeSyncedRegistry(SPELL_RESULT_TYPES_KEY);
    
    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation(IncompCore.MODID, name));
    }
    private static <T> Registry<T> makeSyncedRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).sync(true).create();
    }
    private static <T> Registry<T> registerSimpleWithIntrusiveHolders(ResourceKey<? extends Registry<T>> registryKey) {
        return new MappedRegistry<>(registryKey, Lifecycle.stable(), true);
    }
    public static void register(IEventBus bus) {
        bus.addListener(NewRegistryEvent.class, event -> event.register(CLASS_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(SPECIES_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(CLASS_ABILITY_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(CLASS_PASSIVE_EFFECT_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(MANA_REGEN_CONDITION_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(SPECIES_BEHAVIOUR_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(SPELL_RESULT_TYPE));
    }
}
