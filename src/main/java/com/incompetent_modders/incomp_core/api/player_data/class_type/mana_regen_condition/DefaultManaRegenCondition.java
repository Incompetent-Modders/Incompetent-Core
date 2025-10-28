package com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition;

import com.incompetent_modders.incomp_core.common.registry.ModManaRegenConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

public class DefaultManaRegenCondition extends ManaRegenCondition {
    
    public static final MapCodec<DefaultManaRegenCondition> CODEC = MapCodec.unit(new DefaultManaRegenCondition());
    

    public DefaultManaRegenCondition() {
    }
    @Override
    public boolean apply(Level level, LivingEntity player) {
        return true;
    }
    
    @Override
    public ManaRegenConditionType<? extends ManaRegenCondition> getType() {
        return ModManaRegenConditions.DEFAULT.get();
    }
}
