package com.incompetent_modders.incomp_core.api.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MagicallyAppliedEffect extends MobEffect {
    protected MagicallyAppliedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public List<ItemStack> getCurativeItems() {
        //No curative items by default
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        return ret;
    }
}
