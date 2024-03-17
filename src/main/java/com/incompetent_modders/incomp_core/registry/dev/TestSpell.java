package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellCategory;
import com.incompetent_modders.incomp_core.util.ClientUtils;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TestSpell extends Spell {
    
    public TestSpell() {
        super(false, 0, 5, 120, SpellCategory.MOBILITY);
    }
    
    @Override
    public void onCast(Level level, Player player, InteractionHand hand) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
    }
    @Override
    public void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
    }
}
