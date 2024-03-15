package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptySpell extends Spell {
    public EmptySpell() {
        super(false, 0, 0, 0, SpellCategory.DEBUFF);
    }
    
    @Override
    protected void onCast(Level level, Player player, InteractionHand hand) {
        doNothing(level, player);
    }
    
    @Override
    protected void onFail(Level level, Player player, InteractionHand hand) {
        doNothing(level, player);
    }
}
