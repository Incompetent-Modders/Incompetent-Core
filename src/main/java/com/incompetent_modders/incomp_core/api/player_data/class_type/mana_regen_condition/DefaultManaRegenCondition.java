package com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition;

import com.incompetent_modders.incomp_core.registry.ModManaRegenConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Random;

public class DefaultManaRegenCondition extends ManaRegenCondition {
    
    public static final MapCodec<DefaultManaRegenCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(result -> {
                return result.chance;
            })
    ).apply(instance, DefaultManaRegenCondition::new));
    
    private final float chance;
    
    public DefaultManaRegenCondition(float chance) {
        this.chance = chance;
    }
    @Override
    public boolean apply(Level level, ServerPlayer player) {
        float random = new Random().nextFloat();
        return random <= chance;
    }
    
    @Override
    public ManaRegenConditionType<? extends ManaRegenCondition> getType() {
        return ModManaRegenConditions.DEFAULT.get();
    }
}
