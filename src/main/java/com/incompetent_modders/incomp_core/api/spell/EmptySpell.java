package com.incompetent_modders.incomp_core.api.spell;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptySpell extends Spell {
    public EmptySpell() {
        super(SpellCategory.DEBUFF);
    }
    
    @Override
    protected void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Player player) {
            doNothing(level, player);
        }
    }
    
    @Override
    protected void onFail(Level level, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Player player) {
            doNothing(level, player);
        }
    }
}
