package com.incompetent_modders.incomp_core.api.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;

public class EffectExtendingItem extends Item {
    private final Holder<MobEffect> effectToExtend;
    private final int extensionDuration;
    public EffectExtendingItem(Holder<MobEffect> effect, int extendDuration) {
        super(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).fast().build()));
        effectToExtend = effect;
        extensionDuration = extendDuration;
    }
    
    public Holder<MobEffect> getExtendingEffect() {
        return effectToExtend;
    }
    
    public int getExtendDuration() {
        return extensionDuration;
    }
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().equals(getExtendingEffect())) {
                entity.addEffect(new MobEffectInstance(getExtendingEffect(), effect.getDuration() + getExtendDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                return entity.eat(level, stack);
            } else {
                return stack;
            }
        }
        return stack.has(DataComponents.FOOD) ? entity.eat(level, stack) : stack;
    }
}
