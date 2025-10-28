package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModClassAttributes {
    public static final DeferredRegister<ClassAttributeType<?>> CLASS_ATTRIBUTES = DeferredRegister.create(ModRegistries.CLASS_ATTRIBUTE_TYPE, MODID);

    public static final DeferredHolder<ClassAttributeType<?>, ClassAttributeType<ClassTickFrequency>> TICK_FREQUENCY = CLASS_ATTRIBUTES.register("tick_frequency", () -> new ClassAttributeType<>(ClassTickFrequency.CODEC));
    public static final DeferredHolder<ClassAttributeType<?>, ClassAttributeType<ClassSpellCasting>> SPELL_CASTING = CLASS_ATTRIBUTES.register("spell_casting", () -> new ClassAttributeType<>(ClassSpellCasting.CODEC));
    public static final DeferredHolder<ClassAttributeType<?>, ClassAttributeType<ClassRestrictArmor>> RESTRICT_ARMOR = CLASS_ATTRIBUTES.register("restrict_armor", () -> new ClassAttributeType<>(ClassRestrictArmor.CODEC));
    public static final DeferredHolder<ClassAttributeType<?>, ClassAttributeType<ClassApplyEffect>> APPLY_EFFECT = CLASS_ATTRIBUTES.register("apply_effect", () -> new ClassAttributeType<>(ClassApplyEffect.CODEC));
    public static final DeferredHolder<ClassAttributeType<?>, ClassAttributeType<ClassPreventItemUse>> PREVENT_ITEM_USE = CLASS_ATTRIBUTES.register("prevent_item_use", () -> new ClassAttributeType<>(ClassPreventItemUse.CODEC));

    public static void register(IEventBus eventBus) {
        CLASS_ATTRIBUTES.register(eventBus);
    }
}
