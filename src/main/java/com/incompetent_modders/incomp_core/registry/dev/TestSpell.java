package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellCategory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TestSpell extends Spell {
    
    public TestSpell() {
        super(99, 5, 120, SpellCategory.MOBILITY);
    }
    
    public ItemStack getSpellCatalyst() {
        return Items.BLAZE_POWDER.getDefaultInstance();
    }
    @Override
    public void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        super.onCast(level, entity, hand);
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
    }
}
