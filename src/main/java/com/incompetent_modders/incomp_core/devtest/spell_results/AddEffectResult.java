package com.incompetent_modders.incomp_core.devtest.spell_results;

import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.registry.ModSpellResultTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class AddEffectResult extends SpellResult {
    
    public static final MapCodec<AddEffectResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(AddEffectResult::getEffect),
            Codec.INT.fieldOf("duration").forGetter(AddEffectResult::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(AddEffectResult::getAmplifier)
    ).apply(instance, AddEffectResult::new));
    
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;
    
    public AddEffectResult(Holder<MobEffect> effect, int duration, int amplifier) {
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
    public void execute(Player player) {
        player.addEffect(new MobEffectInstance(getEffect(), getDuration(), getAmplifier()));
    }
    
    @Override
    public SpellResultType<? extends SpellResult> getType() {
        return ModSpellResultTypes.ADD_EFFECT.get();
    }
}
