package com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type;

import com.incompetent_modders.incomp_core.common.registry.ModSpeciesBehaviourTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DefaultSpeciesBehaviour extends SpeciesBehaviour {
    public static final MapCodec<DefaultSpeciesBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("debug", false).forGetter(result -> {
                return result.debug;
            })
    ).apply(instance, DefaultSpeciesBehaviour::new));
    
    private final boolean debug;
    
    public DefaultSpeciesBehaviour(boolean debug) {
        this.debug = debug;
    }
    @Override
    public void apply(Level level, LivingEntity entity) {
    
    }
    
    @Override
    public SpeciesBehaviourType<? extends SpeciesBehaviour> getType() {
        return ModSpeciesBehaviourTypes.DEFAULT.get();
    }
}
