package com.incompetent_modders.incomp_core.core.def.abilities;

import com.incompetent_modders.incomp_core.api.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.common.registry.ModAbilities;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RunFunctionAbility extends Ability {
    public static final MapCodec<RunFunctionAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CacheableFunction.CODEC.optionalFieldOf("function").forGetter(RunFunctionAbility::function)
    ).apply(instance, RunFunctionAbility::new));
    
    private final Optional<CacheableFunction> function;
    
    public RunFunctionAbility(Optional<CacheableFunction> function) {
        this.function = function;
    }
    public Optional<CacheableFunction> function() {
        return function;
    }
    @Override
    public void apply(int classLevel, Level level, Player player) {
        MinecraftServer minecraftserver = player.getServer();
        if (minecraftserver != null) {
            this.function.flatMap((function) -> {
                return function.get(minecraftserver.getFunctions());
            }).ifPresent((commandFunction) -> {
                minecraftserver.getFunctions().execute(commandFunction, player.createCommandSourceStack().withEntity(player).withSuppressedOutput().withPermission(2));
            });
        }
    }

    @Override
    public List<Component> description(Player player) {
        return List.of();
    }

    @Override
    public AbilityType<? extends Ability> getType() {
        return ModAbilities.RUN_FUNCTION.get();
    }
}
