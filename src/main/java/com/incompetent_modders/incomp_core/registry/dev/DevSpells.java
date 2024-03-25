package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class DevSpells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(ModRegistries.SPELL, MODID);
    
    public static final DeferredHolder<Spell, Spell> TEST_SPELL = SPELLS.register("test_spell", TestSpell::new);
    public static final DeferredHolder<Spell, Spell> TEST_PROJECTILE_SPELL = SPELLS.register("test_ranged_spell", TestRangedSpell::new);
    public static final DeferredHolder<Spell, Spell> TEST_PRE_CAST_SPELL = SPELLS.register("test_precast_spell", TestPreCastSpell::new);
    public static final DeferredHolder<Spell, Spell> TEST_BLOCK_PRE_CAST_SPELL = SPELLS.register("test_block_precast_spell", TestBlockPreCastSpell::new);
    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
