package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.core.def.attributes.species.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModSpeciesAttributes {
    public static final DeferredRegister<SpeciesAttributeType<?>> SPECIES_ATTRIBUTES = DeferredRegister.create(ModRegistries.SPECIES_ATTRIBUTE_TYPE, MODID);

    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<BurnInSunlightAttribute>> BURN_IN_SUNLIGHT = SPECIES_ATTRIBUTES.register("burn_in_sunlight", () -> new SpeciesAttributeType<>(BurnInSunlightAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<BurnInLightAttribute>> BURN_IN_LIGHT = SPECIES_ATTRIBUTES.register("burn_in_light", () -> new SpeciesAttributeType<>(BurnInLightAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<EntityAttributeAttribute>> ENTITY_ATTRIBUTES = SPECIES_ATTRIBUTES.register("entity_attributes", () -> new SpeciesAttributeType<>(EntityAttributeAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<DietAttribute>> DIET = SPECIES_ATTRIBUTES.register("diet", () -> new SpeciesAttributeType<>(DietAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<InvertHealAndHarmAttribute>> INVERT_HEAL_HARM = SPECIES_ATTRIBUTES.register("invert_heal_harm", () -> new SpeciesAttributeType<>(InvertHealAndHarmAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<ModifyFoodEffectsAttribute>> MODIFY_FOOD_EFFECTS = SPECIES_ATTRIBUTES.register("modify_food_effects", () -> new SpeciesAttributeType<>(ModifyFoodEffectsAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<TickFrequencyAttribute>> TICK_FREQUENCY = SPECIES_ATTRIBUTES.register("tick_frequency", () -> new SpeciesAttributeType<>(TickFrequencyAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<ApplyEffectAttribute>> APPLY_EFFECT = SPECIES_ATTRIBUTES.register("apply_effect", () -> new SpeciesAttributeType<>(ApplyEffectAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<RunFunctionAttribute>> RUN_FUNCTION = SPECIES_ATTRIBUTES.register("run_function", () -> new SpeciesAttributeType<>(RunFunctionAttribute.CODEC));
    public static final DeferredHolder<SpeciesAttributeType<?>, SpeciesAttributeType<RestrictArmorAttribute>> RESTRICT_ARMOR = SPECIES_ATTRIBUTES.register("restrict_armor", () -> new SpeciesAttributeType<>(RestrictArmorAttribute.CODEC));

    public static void register(IEventBus eventBus) {
        SPECIES_ATTRIBUTES.register(eventBus);
    }
}
