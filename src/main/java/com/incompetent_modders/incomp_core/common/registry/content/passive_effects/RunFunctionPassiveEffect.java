package com.incompetent_modders.incomp_core.common.registry.content.passive_effects;

import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.common.registry.ModClassPassiveEffects;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RunFunctionPassiveEffect extends ClassPassiveEffect {
    public static final MapCodec<RunFunctionPassiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CacheableFunction.CODEC.optionalFieldOf("function").forGetter(RunFunctionPassiveEffect::function)
    ).apply(instance, RunFunctionPassiveEffect::new));
    
    private final Optional<CacheableFunction> function;
    
    public RunFunctionPassiveEffect(Optional<CacheableFunction> function) {
        this.function = function;
    }
    public Optional<CacheableFunction> function() {
        return function;
    }
    
    @Override
    public void apply(Level level, ServerPlayer player) {
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
    public ClassPassiveEffectType<? extends ClassPassiveEffect> getType() {
        return ModClassPassiveEffects.RUN_FUNCTION.get();
    }
}
