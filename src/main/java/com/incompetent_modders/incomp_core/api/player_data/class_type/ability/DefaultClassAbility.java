package com.incompetent_modders.incomp_core.api.player_data.class_type.ability;

import com.incompetent_modders.incomp_core.registry.ModClassAbilities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DefaultClassAbility extends ClassAbility {
    public static final MapCodec<DefaultClassAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("debug", false).forGetter(result -> {
                return result.debug;
            })
    ).apply(instance, DefaultClassAbility::new));
    
    private final boolean debug;
    
    public DefaultClassAbility(boolean debug) {
        this.debug = debug;
    }
    @Override
    public void apply(Level level, Player player) {
    
    }
    
    @Override
    public ClassAbilityType<? extends ClassAbility> getType() {
        return ModClassAbilities.DEFAULT.get();
    }
}
