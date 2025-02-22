package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.effect.SpeciesAlteringEffect;
import com.incompetent_modders.incomp_core.common.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, IncompCore.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> ZOMBIE_VIRUS = EFFECTS.register("zombie_virus", () -> new SpeciesAlteringEffect(MobEffectCategory.NEUTRAL, 0x33703e, Utils.location("zombie")));
    
    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
