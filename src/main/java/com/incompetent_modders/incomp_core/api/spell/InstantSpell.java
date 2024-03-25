package com.incompetent_modders.incomp_core.api.spell;

import net.minecraft.resources.ResourceLocation;

public class InstantSpell extends Spell {
    public InstantSpell(boolean isRangedAttack, int manaCost, int coolDown, SpellCategory category, ResourceLocation casterClassType) {
        super(manaCost, 0, coolDown, category, casterClassType);
    }
    public InstantSpell(boolean isRangedAttack, int manaCost, int coolDown, SpellCategory category) {
        super(manaCost, 0, coolDown, category);
    }
}
