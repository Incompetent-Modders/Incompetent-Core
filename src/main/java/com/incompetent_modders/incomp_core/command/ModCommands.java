package com.incompetent_modders.incomp_core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.function.Predicate;

public class ModCommands {
    public static final Predicate<CommandSourceStack> SOURCE_IS_PLAYER = cs -> cs.getEntity() instanceof Player;
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("incompetent_core")
                .then(CastSpellCommand.register(context))
                .then(SetSpellInSlotCommand.register(context))
                .then(WhatSpellIsInSlotCommand.register())
                .then(ClearSpellSlotsCommand.register())
                .then(RefillManaCommand.register())
                .then(SetSpeciesCommand.register(context))
                .then(ExplodeCommand.register(context))
                ;
        
        LiteralCommandNode<CommandSourceStack> createRoot = dispatcher.register(root);
        
        CommandNode<CommandSourceStack> c = dispatcher.findNode(Collections.singleton("i_c"));
        if (c != null)
            return;
        
        dispatcher.getRoot()
                .addChild(buildRedirect("i_c", createRoot));
    }
    
    
    
    public static LiteralCommandNode<CommandSourceStack> buildRedirect(final String alias, final LiteralCommandNode<CommandSourceStack> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder
                .<CommandSourceStack>literal(alias)
                .requires(destination.getRequirement())
                .forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<CommandSourceStack> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }
}
