package com.incompetent_modders.incomp_core.api.player_data.class_type.ability;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class Ability {
    public static final Codec<Ability> DIRECT_CODEC = Codec.lazyInitialized(ModRegistries.ABILITY_TYPE::byNameCodec)
            .dispatch(Ability::getType, AbilityType::codec);
    
    public abstract void apply(Level level, Player player);
    
    public abstract AbilityType<? extends Ability> getType();
}
