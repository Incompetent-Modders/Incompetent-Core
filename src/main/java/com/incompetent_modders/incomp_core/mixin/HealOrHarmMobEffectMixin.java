package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import net.minecraft.world.effect.HealOrHarmMobEffect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HealOrHarmMobEffect.class)
public class HealOrHarmMobEffectMixin {
    @Mutable
    @Final
    @Shadow
    private boolean isHarm;
    
    @Inject(method = "applyEffectTick", at = @At("HEAD"))
    private void applyEffectTick(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(entity);
        if (isHarm == speciesType.invertHealAndHarm()) {
            entity.heal((float)Math.max(4 << amplifier, 0));
            cir.cancel();
        } else {
            entity.hurt(entity.damageSources().magic(), (float)(6 << amplifier));
            cir.cancel();
        }
    }
    
    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"), cancellable = true)
    private void applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity entity, int amplifier, double health, CallbackInfo ci) {
        int j;
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(entity);
        if (isHarm == speciesType.invertHealAndHarm()) {
            j = (int)(health * (double)(4 << amplifier) + 0.5);
            entity.heal((float)j);
            ci.cancel();

        } else {
            j = (int)(health * (double)(6 << amplifier) + 0.5);
            if (source == null) {
                entity.hurt(entity.damageSources().magic(), (float)j);
                ci.cancel();
            } else {
                entity.hurt(entity.damageSources().indirectMagic(source, indirectSource), (float)j);
                ci.cancel();
            }
        }
    }
}
