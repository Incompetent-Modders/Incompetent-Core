package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.api.json.spell.SpellPropertyListener;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.command.arguments.SpellArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class CastSpellCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("cast_spell")
                        .requires(player -> player.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(
                                                Commands.argument("spells", new SpellArgument())
                                                        .executes(
                                                                exec -> castSpell(
                                                                        exec.getSource(), SpellArgument.getSpell(exec, "spells"), EntityArgument.getPlayers(exec, "targets"), 1
                                                                )
                                                        )
                                        )
                        );
    }
    private static int castSpell(CommandSourceStack source, Spell spell, Collection<ServerPlayer> targets, int count) {
        for (ServerPlayer serverplayer : targets) {
            for (int i = 0; i < count; ++i) {
                SpellPropertyListener.getSpellProperties(spell).executeCastNoRequirements(serverplayer);
            }
        }
        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.cast_spell.success.single", spell.getDisplayName(), count, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.cast_spell.success.single", spell.getDisplayName(), count, targets.size()), true);
        }
        return targets.size();
    }
    
}
