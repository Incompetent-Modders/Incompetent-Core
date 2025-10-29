package com.incompetent_modders.incomp_core.common.registry.content.passive_effects;

import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.common.registry.ModClassPassiveEffects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ApplyEffectPassiveEffect extends ClassPassiveEffect {
    public static final MapCodec<ApplyEffectPassiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ApplyEffectPassiveEffect::getEffect),
            Codec.INT.fieldOf("duration").forGetter(ApplyEffectPassiveEffect::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(ApplyEffectPassiveEffect::getAmplifier)
    ).apply(instance, ApplyEffectPassiveEffect::new));
    
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;
    
    public ApplyEffectPassiveEffect(Holder<MobEffect> effect, int duration, int amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }
    
    public Holder<MobEffect> getEffect() {
        return effect;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public int getAmplifier() {
        return amplifier;
    }
    
    @Override
    public void apply(Level level, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(getEffect(), getDuration(), getAmplifier()));
    }
    
    @Override
    public ClassPassiveEffectType<? extends ClassPassiveEffect> getType() {
        return ModClassPassiveEffects.APPLY_EFFECT.get();
    }
}
