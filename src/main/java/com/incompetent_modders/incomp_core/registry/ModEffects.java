package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.spell.pre_cast.ArcaneSelectionEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, IncompCore.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> ARCANE_SELECTION = EFFECTS.register("arcane_selection", ArcaneSelectionEffect::new);
    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
