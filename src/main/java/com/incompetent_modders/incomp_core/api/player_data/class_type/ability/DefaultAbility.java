package com.incompetent_modders.incomp_core.api.player_data.class_type.ability;

import com.incompetent_modders.incomp_core.api.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.common.registry.ModAbilities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DefaultAbility extends Ability {
    public static final MapCodec<DefaultAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("debug", false).forGetter(result -> {
                return result.debug;
            })
    ).apply(instance, DefaultAbility::new));
    
    private final boolean debug;
    
    public DefaultAbility(boolean debug) {
        this.debug = debug;
    }
    @Override
    public void apply(int classLevel, Level level, Player player) {
        if (debug) {
            System.out.println("Default class ability applied to player " + player.getName().getString());
        }
    }
    
    @Override
    public AbilityType<? extends Ability> getType() {
        return ModAbilities.DEFAULT.get();
    }
}
