package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class DevSpells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(ModRegistries.SPELL, MODID);
    
    public static final DeferredHolder<Spell, Spell> TEST_SPELL = SPELLS.register("test_spell", Spell::new);
    public static final DeferredHolder<Spell, Spell> TEST_PROJECTILE_SPELL = SPELLS.register("test_ranged_spell", Spell::new);
    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
