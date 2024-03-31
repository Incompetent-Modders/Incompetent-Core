package com.incompetent_modders.incomp_core.api.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.EffectCure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MagicallyAppliedEffect extends MobEffect {
    protected MagicallyAppliedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        //No curative items by default
        cures.clear();
    }
}
