package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModSpells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(ModRegistries.SPELL, MODID);
    
    public static final DeferredHolder<Spell, Spell> EMPTY = SPELLS.register("empty", Spell::new);
}
