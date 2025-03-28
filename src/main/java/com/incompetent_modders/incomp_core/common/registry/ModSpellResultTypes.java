package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.common.registry.content.spell_results.*;
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
    public static final DeferredHolder<SpellResultType<?>, SpellResultType<ReplaceCatalystResult>> REPLACE_CATALYST = SPELL_RESULT_TYPES.register("replace_catalyst", () -> new SpellResultType<>(ReplaceCatalystResult.CODEC));
    public static final DeferredHolder<SpellResultType<?>, SpellResultType<DamageCatalystResult>> DAMAGE_CATALYST = SPELL_RESULT_TYPES.register("damage_catalyst", () -> new SpellResultType<>(DamageCatalystResult.CODEC));
    public static void register(IEventBus eventBus) {
        SPELL_RESULT_TYPES.register(eventBus);
    }
}
