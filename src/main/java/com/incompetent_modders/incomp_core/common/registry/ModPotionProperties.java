package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.PotionProperty;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;

public class ModPotionProperties {

    public static final ResourceKey<PotionProperty> TEST = create("test");

    private static ResourceKey<PotionProperty> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.POTION_PROPERTY, IncompCore.makeId(name));
    }

    private static void registerProperties(BootstrapContext<PotionProperty> context) {
        register(context, TEST, PotionProperty.builder(MobEffects.MOVEMENT_SPEED).manaRegenModifier(2.0f));
    }

    private static void register(BootstrapContext<PotionProperty> context, ResourceKey<PotionProperty> key, PotionProperty.Builder potionBuilder) {
        context.register(key, potionBuilder.build());
    }

    public static void bootstrap(BootstrapContext<PotionProperty> context) {
        registerProperties(context);
    }
}
