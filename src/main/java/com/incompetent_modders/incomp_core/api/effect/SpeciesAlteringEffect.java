package com.incompetent_modders.incomp_core.api.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SpeciesAlteringEffect extends MagicallyAppliedEffect {
    private static ResourceLocation CONVERT_TO;
    public SpeciesAlteringEffect(MobEffectCategory category, int color, ResourceLocation convertTo) {
        super(category, color);
        CONVERT_TO = convertTo;
    }
    
    public ResourceLocation getConvertTo() {
        return CONVERT_TO;
    }
    
    @SuppressWarnings("unchecked")
    public boolean applyEffectTick(LivingEntity affected, int amplifier) {
        int i = affected.getEffect((Holder<MobEffect>) this).getDuration();
        if (!affected.getCommandSenderWorld().isClientSide()) {
            if (i <= 1200) {
                affected.level().playLocalSound(affected.getX(), affected.getY(), affected.getZ(), SoundEvents.BREEZE_HURT, affected.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
        return true;
    }
}
