package com.incompetent_modders.incomp_core.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ToggleWelcomeMessageCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("toggleWelcomeMessage").requires(s -> s.hasPermission(0)).executes(arguments -> {
            return toggleWelcomeMessage(arguments.getSource());
        });
    }
    
    private static int toggleWelcomeMessage(CommandSourceStack source) {
        Player player = source.getPlayer();
        if (player != null) {
            CompoundTag playerNBT = player.getPersistentData();
            playerNBT.putBoolean("incompetent_core:welcome_message", !playerNBT.getBoolean("incompetent_core:welcome_message"));
            Component outputComponent = Component.translatable(playerNBT.getBoolean("incompetent_core:welcome_message") ? "commands.incompetent_core.welcome_message.enabled" : "commands.incompetent_core.welcome_message.disabled");
            source.sendSuccess(() -> outputComponent, true);
        }
        return 0;
    }
}
