package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Objects;

public class CastSpellCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("cast_spell")
                        .requires(player -> player.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(
                                                Commands.argument("spells", ResourceArgument.resource(context, ModRegistries.Keys.SPELL))
                                                        .executes(
                                                                exec -> castSpell(
                                                                        exec.getSource(), ResourceArgument.getResource(exec, "spells", ModRegistries.Keys.SPELL), EntityArgument.getPlayers(exec, "targets")
                                                                )
                                                        )
                                        )
                        );
    }
    private static int castSpell(CommandSourceStack source, Holder.Reference<Spell> spell, Collection<ServerPlayer> targets) {
        for (ServerPlayer serverplayer : targets) {
            for (int i = 0; i < targets.size(); ++i) {
                spell.value().executeCast(serverplayer.level(), serverplayer);
            }
        }
        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.cast_spell.success.single", Spell.getDisplayName(Objects.requireNonNull(spell.getKey())), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.cast_spell.success.all", Spell.getDisplayName(Objects.requireNonNull(spell.getKey())), targets.size(), playerNameList(targets)), true);
        }
        return targets.size();
    }
    
    private static String playerNameList(Collection<ServerPlayer> players) {
        StringBuilder builder = new StringBuilder();
        for (ServerPlayer player : players) {
            builder.append(player.getDisplayName());
            builder.append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }
    
}
