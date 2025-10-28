package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;

public class SetClassCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("setClass").requires(s -> s.hasPermission(2)).then(Commands.argument("class", ResourceArgument.resource(context, ModRegistries.Keys.CLASS_TYPE)).executes(arguments -> {
            Player player = (Player) arguments.getSource().getEntity();
            Holder.Reference<ClassType> classType = ResourceArgument.getResource(arguments, "class", ModRegistries.Keys.CLASS_TYPE);
            if (player == null)
                return 0;
            PlayerDataHelper.setClassType(player, classType.getKey());
            return 0;
        }));
    }
}
