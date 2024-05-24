package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    private void applyEffectTick(LivingEntity p_295255_, int p_295147_, CallbackInfoReturnable<Boolean> cir) {
        if (p_295255_ instanceof Player player) {
            if (SpeciesData.Get.playerSpecies(player) == null)
                cir.cancel();
            if (isHarm == SpeciesData.Get.isInvertedHealAndHarm(player)) {
                p_295255_.heal((float)Math.max(4 << p_295147_, 0));
                cir.cancel();
            } else {
                p_295255_.hurt(p_295255_.damageSources().magic(), (float)(6 << p_295147_));
                cir.cancel();
            }
        }
    }
    
    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"), cancellable = true)
    private void applyInstantenousEffect(Entity p_294574_, Entity p_295692_, LivingEntity p_296483_, int p_296095_, double p_295178_, CallbackInfo ci) {
        if (p_296483_ instanceof Player player) {
            int j;
            if (SpeciesData.Get.playerSpecies(player) == null)
                ci.cancel();
            if (isHarm == SpeciesData.Get.isInvertedHealAndHarm(player)) {
                j = (int)(p_295178_ * (double)(4 << p_296095_) + 0.5);
                p_296483_.heal((float)j);
                ci.cancel();
                
            } else {
                j = (int)(p_295178_ * (double)(6 << p_296095_) + 0.5);
                if (p_294574_ == null) {
                    p_296483_.hurt(p_296483_.damageSources().magic(), (float)j);
                    ci.cancel();
                } else {
                    p_296483_.hurt(p_296483_.damageSources().indirectMagic(p_294574_, p_295692_), (float)j);
                    ci.cancel();
                }
            }
        }
    }
}
