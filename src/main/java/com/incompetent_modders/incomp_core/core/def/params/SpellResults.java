package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.common.registry.content.spell_results.DoNothingResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record SpellResults(Optional<SpellResult> spellResult, Optional<CacheableFunction> function) {
    public static final Codec<SpellResults> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellResult.DIRECT_CODEC.optionalFieldOf("spell_result").forGetter(SpellResults::spellResult),
            CacheableFunction.CODEC.optionalFieldOf("function").forGetter(SpellResults::function)
    ).apply(instance, SpellResults::new));
    
    public static SpellResults EMPTY = new SpellResults(Optional.of(new DoNothingResult(true)), Optional.empty());

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SpellResults(Optional<SpellResult> spellResult, Optional<CacheableFunction> function) {
        this.spellResult = spellResult;
        this.function = function;
    }
    
    public Optional<SpellResult> spellResult() {
        return spellResult;
    }
    
    public Optional<CacheableFunction> function() {
        return function;
    }

    public static SpellResults create(SpellResult spellResult) {
        return new SpellResults(Optional.of(spellResult), Optional.empty());
    }

    public static SpellResults create(CacheableFunction function) {
        return new SpellResults(Optional.empty(), Optional.of(function));
    }
    
    public void execute(Player player) {
        this.spellResult.ifPresent((spellResult) -> {
            spellResult.execute(player);
        });
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer minecraftserver = serverPlayer.server;
            this.function.flatMap((function) -> {
                return function.get(minecraftserver.getFunctions());
            }).ifPresent((commandFunction) -> {
                minecraftserver.getFunctions().execute(commandFunction, player.createCommandSourceStack().withSuppressedOutput().withPermission(2));
            });
        }
    }
}
