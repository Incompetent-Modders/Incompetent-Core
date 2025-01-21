package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EffectExtendingItem extends Item {
    private final ResourceKey<MobEffect> effectToExtendKey;
    private final int extensionDuration;
    public EffectExtendingItem(ResourceKey<MobEffect> effectKey, int extendDuration) {
        super(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).fast().build()));
        effectToExtendKey = effectKey;
        extensionDuration = extendDuration;
    }
    
    public ResourceKey<MobEffect> getExtendingEffect() {
        return effectToExtendKey;
    }
    public int getExtendDuration() {
        return extensionDuration;
    }

    public ResourceKey<MobEffect> getExtendingEffect(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STORED_EFFECT_POSTPONE.get(), Objects.requireNonNull(MobEffects.MOVEMENT_SPEED.getKey()));
    }
    public int getExtendDuration(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.EFFECT_POSTPONE_DURATION.get(), 600);
    }
    public MobEffectInstance mobeffectinstance(ItemStack stack) {
        return new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolder(getExtendingEffect(stack)).get(), getExtendDuration(stack), 0, false, false, true);
    }
    public int getColour(ItemStack stack) {
        return mobeffectinstance(stack).getEffect().value().getColor();
    }
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        for (MobEffectInstance effect : effects) {
            Holder<MobEffect> extendingEffect = BuiltInRegistries.MOB_EFFECT.getHolder(getExtendingEffect(stack)).isPresent() ? BuiltInRegistries.MOB_EFFECT.getHolder(getExtendingEffect(stack)).get() : MobEffects.MOVEMENT_SPEED;
            if (effect.getEffect().equals(extendingEffect)) {
                entity.addEffect(new MobEffectInstance(extendingEffect, effect.getDuration() + getExtendDuration(stack), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                return entity.eat(level, stack);
            } else {
                return stack;
            }
        }
        return stack.has(DataComponents.FOOD) ? entity.eat(level, stack) : stack;
    }
    
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (!stack.has(ModDataComponents.STORED_EFFECT_POSTPONE.get())) {
            stack.set(ModDataComponents.STORED_EFFECT_POSTPONE.get(), getExtendingEffect());
        }
        if (!stack.has(ModDataComponents.EFFECT_POSTPONE_DURATION.get())) {
            stack.set(ModDataComponents.EFFECT_POSTPONE_DURATION.get(), getExtendDuration());
        }
    }
    
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MutableComponent mutablecomponent;
        mutablecomponent = Component.translatable(mobeffectinstance(stack).getDescriptionId());
        tooltip.add(mutablecomponent.append(" (+ " + StringUtil.formatTickDuration(getExtendDuration(stack), context.tickRate()) + ")").withStyle(mobeffectinstance(stack).getEffect().value().getCategory().getTooltipFormatting()));
    }
}
