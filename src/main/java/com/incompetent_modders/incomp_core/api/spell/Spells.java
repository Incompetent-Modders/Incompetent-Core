package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.ModRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class Spells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(ModRegistries.SPELL, MODID);
    
    public static final DeferredHolder<Spell, Spell> EMPTY = SPELLS.register("empty", EmptySpell::new);
}
