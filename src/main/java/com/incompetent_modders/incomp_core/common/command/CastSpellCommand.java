package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import com.incompetent_modders.incomp_core.common.command.arguments.SpellArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class CastSpellCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("cast_spell")
                        .requires(player -> player.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(
                                                Commands.argument("spells", new SpellArgument())
                                                        .executes(
                                                                exec -> castSpell(
                                                                        exec.getSource(), SpellArgument.getSpell(exec, "spells"), EntityArgument.getPlayers(exec, "targets")
                                                                )
                                                        )
                                        )
                        );
    }
    private static int castSpell(CommandSourceStack source, ResourceLocation spell, Collection<ServerPlayer> targets) {
        for (ServerPlayer serverplayer : targets) {
            for (int i = 0; i < targets.size(); ++i) {
                SpellListener.getSpellProperties(spell).executeCast(serverplayer.level(), serverplayer);
            }
        }
        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.cast_spell.success.single", ClientSpellManager.getDisplayName(spell), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.cast_spell.success.all", ClientSpellManager.getDisplayName(spell), targets.size(), playerNameList(targets)), true);
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
