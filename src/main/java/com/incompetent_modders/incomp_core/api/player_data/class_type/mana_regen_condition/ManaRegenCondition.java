package com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public abstract class ManaRegenCondition {
    public static final Codec<ManaRegenCondition> DIRECT_CODEC = Codec.lazyInitialized(ModRegistries.MANA_REGEN_CONDITION_TYPE::byNameCodec)
            .dispatch(ManaRegenCondition::getType, ManaRegenConditionType::codec);
    
    public abstract boolean apply(Level level, ServerPlayer player);
    
    public abstract ManaRegenConditionType<? extends ManaRegenCondition> getType();
}
