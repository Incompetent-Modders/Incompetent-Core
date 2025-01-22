package com.incompetent_modders.incomp_core.api.player_data.class_type.passive;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class ClassPassiveEffect {
    public static final Codec<ClassPassiveEffect> DIRECT_CODEC = Codec.lazyInitialized(ModRegistries.CLASS_PASSIVE_EFFECT_TYPE::byNameCodec)
            .dispatch(ClassPassiveEffect::getType, ClassPassiveEffectType::codec);
    
    public abstract void apply(Level level, LivingEntity entity);
    
    public abstract ClassPassiveEffectType<? extends ClassPassiveEffect> getType();
}
