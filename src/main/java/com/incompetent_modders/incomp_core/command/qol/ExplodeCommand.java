package com.incompetent_modders.incomp_core.command.qol;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplodeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        
        return Commands.literal("explode").requires(s -> s.hasPermission(2)).then(Commands.argument("location", Vec3Argument.vec3())
                .then(Commands.argument("power", FloatArgumentType.floatArg()).executes(arguments -> {
                    return explode(arguments.getSource(), Vec3Argument.getCoordinates(arguments, "location"), FloatArgumentType.getFloat(arguments, "power"));
                }
        ).then(Commands.argument("fire", BoolArgumentType.bool()).executes(arguments -> {
                    return explode(arguments.getSource(), Vec3Argument.getCoordinates(arguments, "location"), FloatArgumentType.getFloat(arguments, "power"), BoolArgumentType.getBool(arguments, "fire"));
                }
        ).then(Commands.argument("smallExplosionParticles", ParticleArgument.particle(context))
          .then(Commands.argument("largeExplosionParticles", ParticleArgument.particle(context)).executes(arguments -> {
                    return explode(arguments.getSource(), Vec3Argument.getCoordinates(arguments, "location"), FloatArgumentType.getFloat(arguments, "power"), BoolArgumentType.getBool(arguments, "fire"), ParticleArgument.getParticle(arguments, "smallExplosionParticles"), ParticleArgument.getParticle(arguments, "largeExplosionParticles"));
                }
        ).then(Commands.argument("explosionSound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((arguments) -> {
                    return explode(arguments.getSource(), Vec3Argument.getCoordinates(arguments, "location"), FloatArgumentType.getFloat(arguments, "power"), BoolArgumentType.getBool(arguments, "fire"), ParticleArgument.getParticle(arguments, "smallExplosionParticles"), ParticleArgument.getParticle(arguments, "largeExplosionParticles"), ResourceLocationArgument.getId(arguments, "explosionSound"));
                }
        )))))));
        
    }
    
    private static int explode(CommandSourceStack source, Coordinates coordinates, float power) {
        Player player = (Player) source.getEntity();
        if (player != null) {
            Level level = player.level();
            Vec3 vec3 = coordinates.getPosition(source);
            level.explode(player, vec3.x, vec3.y, vec3.z, power, Level.ExplosionInteraction.BLOCK);
        }
        return 0;
    }
    private static int explode(CommandSourceStack source, Coordinates coordinates, float power, boolean fire) {
        Player player = (Player) source.getEntity();
        if (player != null) {
            Level level = player.level();
            Vec3 vec3 = coordinates.getPosition(source);
            level.explode(player, vec3.x, vec3.y, vec3.z, power, fire, Level.ExplosionInteraction.BLOCK);
        }
        return 0;
    }
    private static int explode(CommandSourceStack source, Coordinates coordinates, float power, boolean fire, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles) {
        Player player = (Player) source.getEntity();
        if (player != null) {
            Level level = player.level();
            Vec3 vec3 = coordinates.getPosition(source);
            level.explode(player, Explosion.getDefaultDamageSource(level, player), null, vec3.x, vec3.y, vec3.z, power, fire, Level.ExplosionInteraction.BLOCK, smallExplosionParticles, largeExplosionParticles, SoundEvents.GENERIC_EXPLODE);
        }
        return 0;
    }
    private static int explode(CommandSourceStack source, Coordinates coordinates, float power, boolean fire, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, ResourceLocation soundLoc) {
        Player player = (Player) source.getEntity();
        Holder<SoundEvent> holder = Holder.direct(SoundEvent.createVariableRangeEvent(soundLoc));
        if (player != null) {
            Level level = player.level();
            Vec3 vec3 = coordinates.getPosition(source);
            level.explode(player, Explosion.getDefaultDamageSource(level, player), null, vec3.x, vec3.y, vec3.z, power, fire, Level.ExplosionInteraction.BLOCK, smallExplosionParticles, largeExplosionParticles, holder);
        }
        return 0;
    }
}
