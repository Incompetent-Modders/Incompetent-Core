package com.incompetent_modders.incomp_core.api.player_data.class_type.ability;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class ClassAbility {
    public static final Codec<ClassAbility> DIRECT_CODEC = Codec.lazyInitialized(ModRegistries.CLASS_ABILITY_TYPE::byNameCodec)
            .dispatch(ClassAbility::getType, ClassAbilityType::codec);
    
    public abstract void apply(Level level, Player player);
    
    public abstract ClassAbilityType<? extends ClassAbility> getType();
}
