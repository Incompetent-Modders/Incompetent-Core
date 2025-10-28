package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.core.def.Spell;
import net.minecraft.resources.ResourceKey;

public record ClientSpellData(SpellData spellData) {

    public static ClientSpellData create(SpellData spellData) {
        return new ClientSpellData(spellData);
    }

    public static ClientSpellData createDefault() {
        return new ClientSpellData(SpellData.createDefault());
    }

    public ClientSpellData setSpell(ResourceKey<Spell> spellKey) {
        return new ClientSpellData(spellData.setSpell(spellKey));
    }
}
