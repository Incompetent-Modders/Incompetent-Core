package com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class SpeciesBehaviour {
    public static final Codec<SpeciesBehaviour> DIRECT_CODEC = Codec.lazyInitialized(ModRegistries.SPECIES_BEHAVIOUR_TYPE::byNameCodec)
            .dispatch(SpeciesBehaviour::getType, SpeciesBehaviourType::codec);
    
    public abstract void apply(Level level, LivingEntity entity);
    
    public abstract SpeciesBehaviourType<? extends SpeciesBehaviour> getType();
    
    
}
