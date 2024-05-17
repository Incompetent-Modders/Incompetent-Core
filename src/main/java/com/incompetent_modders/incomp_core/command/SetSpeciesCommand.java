package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.command.arguments.SpeciesArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class SetSpeciesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("setSpecies").requires(s -> s.hasPermission(2)).then(Commands.argument("species", new SpeciesArgument(buildContext)).executes(arguments -> {
            Player player = (Player) arguments.getSource().getEntity();
            SpeciesType species = SpeciesArgument.getSpecies(arguments, "species").getSpeciesType();
            if (player == null)
                return 0;
            PlayerDataCore.SpeciesData.setSpecies(player, species);
            return 0;
        }));
    }
}
