package com.incompetent_modders.incomp_core.registry.content.abilities;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.registry.ModAbilities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ApplyEffectAbility extends Ability {
    public static final MapCodec<ApplyEffectAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ApplyEffectAbility::getEffect),
            Codec.INT.fieldOf("duration").forGetter(ApplyEffectAbility::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(ApplyEffectAbility::getAmplifier)
    ).apply(instance, ApplyEffectAbility::new));
    
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;
    
    public ApplyEffectAbility(Holder<MobEffect> effect, int duration, int amplifier) {
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
    public void apply(Level level, Player player) {
        player.addEffect(new MobEffectInstance(getEffect(), getDuration(), getAmplifier()));
    }
    
    @Override
    public AbilityType<? extends Ability> getType() {
        return ModAbilities.APPLY_EFFECT.get();
    }
}
