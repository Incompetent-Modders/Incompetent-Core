package com.incompetent_modders.incomp_core.devtest.spell_results;

import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.registry.ModSpellResultTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;
public class RaycastProjectileResult extends SpellResult {
    
    public static final MapCodec<RaycastProjectileResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CacheableFunction.CODEC.optionalFieldOf("function").forGetter(RaycastProjectileResult::function)
    ).apply(instance, RaycastProjectileResult::new));
    
    private final Optional<CacheableFunction> function;
    
    public Optional<CacheableFunction> function() {
        return function;
    }
    
    
    
    public RaycastProjectileResult(Optional<CacheableFunction> function) {
        this.function = function;
    }
    
    @Override
    public void execute(Player player) {
        HitResult result = SpellUtils.genericSpellRayTrace(player);
        if (player instanceof ServerPlayer serverPlayer) {
            onHit(serverPlayer, result);
        }
    }
    
    @Override
    public SpellResultType<? extends SpellResult> getType() {
        return ModSpellResultTypes.RAYCAST_PROJECTILE.get();
    }
    public void onHit(ServerPlayer player, HitResult result) {
        
        MinecraftServer minecraftserver = player.server;
        this.function.flatMap((function) -> {
            return function.get(minecraftserver.getFunctions());
        }).ifPresent((commandFunction) -> {
            minecraftserver.getFunctions().execute(commandFunction, player.createCommandSourceStack().withPosition(result.getLocation()).withSuppressedOutput().withPermission(2));
        });
    }
}
