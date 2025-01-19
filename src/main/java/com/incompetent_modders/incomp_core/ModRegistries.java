package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenConditionType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.core.def.*;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModRegistries {

    public static class Keys {
        public static final ResourceKey<Registry<AbilityType<?>>> ABILITY_TYPE = createRegistryKey("ability_type");
        public static final ResourceKey<Registry<ClassPassiveEffectType<?>>> CLASS_PASSIVE_EFFECT_TYPE = createRegistryKey("class_passive_effect_type");
        public static final ResourceKey<Registry<ManaRegenConditionType<?>>> MANA_REGEN_CONDITION_TYPE = createRegistryKey("mana_regen_condition_type");
        public static final ResourceKey<Registry<SpeciesBehaviourType<?>>> SPECIES_BEHAVIOUR_TYPE = createRegistryKey("species_behaviour_type");
        public static final ResourceKey<Registry<SpellResultType<?>>> SPELL_RESULT_TYPES = createRegistryKey("spell_result_type");
        public static final ResourceKey<Registry<Ability>> ABILITY = createRegistryKey("ability");
        public static final ResourceKey<Registry<Spell>> SPELL = createRegistryKey("spell");
        public static final ResourceKey<Registry<Diet>> DIET = createRegistryKey("diet");
        public static final ResourceKey<Registry<SpeciesType>> SPECIES_TYPE = createRegistryKey("species_type");
        public static final ResourceKey<Registry<ClassType>> CLASS_TYPE = createRegistryKey("class_type");
        public static final ResourceKey<Registry<PotionProperty>> POTION_PROPERTY = createRegistryKey("potion_property");
    }
    
    public static final Registry<AbilityType<?>> ABILITY_TYPE = makeSyncedRegistry(Keys.ABILITY_TYPE);
    public static final Registry<ClassPassiveEffectType<?>> CLASS_PASSIVE_EFFECT_TYPE = makeSyncedRegistry(Keys.CLASS_PASSIVE_EFFECT_TYPE);
    public static final Registry<ManaRegenConditionType<?>> MANA_REGEN_CONDITION_TYPE = makeSyncedRegistry(Keys.MANA_REGEN_CONDITION_TYPE);
    public static final Registry<SpeciesBehaviourType<?>> SPECIES_BEHAVIOUR_TYPE = makeSyncedRegistry(Keys.SPECIES_BEHAVIOUR_TYPE);
    public static final Registry<SpellResultType<?>> SPELL_RESULT_TYPE = makeSyncedRegistry(Keys.SPELL_RESULT_TYPES);
    public static final Registry<Ability> ABILITY = registerSimpleWithIntrusiveHolders(Keys.ABILITY);

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, name));
    }
    private static <T> Registry<T> makeSyncedRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).sync(true).create();
    }
    private static <T> Registry<T> registerSimpleWithIntrusiveHolders(ResourceKey<? extends Registry<T>> registryKey) {
        return new MappedRegistry<>(registryKey, Lifecycle.stable(), true);
    }
    public static void register(IEventBus bus) {
        bus.addListener(NewRegistryEvent.class, event -> event.register(ABILITY_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(CLASS_PASSIVE_EFFECT_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(MANA_REGEN_CONDITION_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(SPECIES_BEHAVIOUR_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(SPELL_RESULT_TYPE));
        bus.addListener(NewRegistryEvent.class, event -> event.register(ABILITY));
    }
}
