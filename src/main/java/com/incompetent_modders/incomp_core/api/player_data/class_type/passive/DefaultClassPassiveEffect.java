package com.incompetent_modders.incomp_core.api.player_data.class_type.passive;

import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.DefaultSpeciesBehaviour;
import com.incompetent_modders.incomp_core.registry.ModClassPassiveEffects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class DefaultClassPassiveEffect extends ClassPassiveEffect {
    public static final MapCodec<DefaultClassPassiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("debug", false).forGetter(result -> {
                return result.debug;
            })
    ).apply(instance, DefaultClassPassiveEffect::new));
    
    private final boolean debug;
    
    public DefaultClassPassiveEffect(boolean debug) {
        this.debug = debug;
    }
    @Override
    public void apply(Level level, ServerPlayer player) {
    
    }
    
    @Override
    public ClassPassiveEffectType<? extends ClassPassiveEffect> getType() {
        return ModClassPassiveEffects.DEFAULT.get();
    }
}
