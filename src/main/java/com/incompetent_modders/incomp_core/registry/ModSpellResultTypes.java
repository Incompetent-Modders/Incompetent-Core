package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.devtest.spell_results.AddEffectResult;
import com.incompetent_modders.incomp_core.devtest.spell_results.DoNothingResult;
import com.incompetent_modders.incomp_core.devtest.spell_results.GiveItemsResult;
import com.incompetent_modders.incomp_core.devtest.spell_results.RaycastProjectileResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModSpellResultTypes {
    public static final DeferredRegister<SpellResultType<?>> SPELL_RESULT_TYPES = DeferredRegister.create(ModRegistries.SPELL_RESULT_TYPE, MODID);
    
    public static final DeferredHolder<SpellResultType<?>, SpellResultType<DoNothingResult>> DO_NOTHING = SPELL_RESULT_TYPES.register("do_nothing", () -> new SpellResultType<>(DoNothingResult.CODEC));
    public static final DeferredHolder<SpellResultType<?>, SpellResultType<AddEffectResult>> ADD_EFFECT = SPELL_RESULT_TYPES.register("add_effect", () -> new SpellResultType<>(AddEffectResult.CODEC));
    public static final DeferredHolder<SpellResultType<?>, SpellResultType<RaycastProjectileResult>> RAYCAST_PROJECTILE = SPELL_RESULT_TYPES.register("raycast", () -> new SpellResultType<>(RaycastProjectileResult.CODEC));
    public static final DeferredHolder<SpellResultType<?>, SpellResultType<GiveItemsResult>> GIVE_ITEMS = SPELL_RESULT_TYPES.register("give_item", () -> new SpellResultType<>(GiveItemsResult.CODEC));
    public static void register(IEventBus eventBus) {
        SPELL_RESULT_TYPES.register(eventBus);
    }
}
