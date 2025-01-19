package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.incompetent_modders.incomp_core.common.command.arguments.SpeciesArgument;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.DamageCommand;
import net.minecraft.world.entity.player.Player;

public class SetSpeciesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("setSpecies").requires(s -> s.hasPermission(2)).then(Commands.argument("species", ResourceArgument.resource(context, ModRegistries.Keys.SPECIES_TYPE)).executes(arguments -> {
            Player player = (Player) arguments.getSource().getEntity();
            Holder.Reference<SpeciesType> species = ResourceArgument.getResource(arguments, "species", ModRegistries.Keys.SPECIES_TYPE);
            if (player == null)
                return 0;
            PlayerDataHelper.setSpeciesType(player, species.getKey());
            return 0;
        }));
    }
}
