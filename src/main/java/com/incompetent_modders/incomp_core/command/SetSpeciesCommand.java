package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.incompetent_modders.incomp_core.command.arguments.SpeciesArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SetSpeciesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("setSpecies").requires(s -> s.hasPermission(2)).then(Commands.argument("species", new SpeciesArgument()).executes(arguments -> {
            Player player = (Player) arguments.getSource().getEntity();
            ResourceLocation species = SpeciesArgument.getSpecies(arguments, "species");
            if (player == null)
                return 0;
            SpeciesData.Set.playerSpecies(player, species);
            return 0;
        }));
    }
}
