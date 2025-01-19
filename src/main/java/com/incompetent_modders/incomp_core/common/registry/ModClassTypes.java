package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.content.passive_effects.ApplyEffectPassiveEffect;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;

public class ModClassTypes {
    public static final ResourceKey<ClassType> NONE = create("none");
    public static final ResourceKey<ClassType> PLAGUE_DOCTOR = create("plague_doctor");

    private static ResourceKey<ClassType> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.CLASS_TYPE, IncompCore.makeId(name));
    }

    private static void registerClasses(BootstrapContext<ClassType> context) {
        register(context, NONE, ClassType.builder());
        register(context, PLAGUE_DOCTOR, ClassType.builder().maxMana(100).canCastSpells(true).passiveEffect(new ApplyEffectPassiveEffect(MobEffects.NIGHT_VISION, 20, 1), 20));
    }

    private static void register(BootstrapContext<ClassType> context, ResourceKey<ClassType> key, ClassType.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<ClassType> context) {
        registerClasses(context);
    }
}
