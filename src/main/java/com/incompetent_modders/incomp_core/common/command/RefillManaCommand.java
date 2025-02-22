package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.api.player.ManaData;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class RefillManaCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("refillMana").requires(s -> s.hasPermission(2)).then(Commands.argument("amount", IntegerArgumentType.integer(0, 1000000000)).executes(arguments -> {
            ServerLevel world = arguments.getSource().getLevel();
            Player player = (Player) arguments.getSource().getEntity();
            int amountToHeal = IntegerArgumentType.getInteger(arguments, "amount");
            if (player == null)
                player = FakePlayerFactory.getMinecraft(world);
            if (ManaData.Get.maxMana(player) < amountToHeal) {
                ManaData.Set.mana(player, ManaData.Get.maxMana(player));
            } else {
                ManaData.Util.healMana(player, amountToHeal);
            }
            return 0;
        })).then(Commands.argument("refillFull", BoolArgumentType.bool()).executes(arguments -> {
            ServerLevel world = arguments.getSource().getLevel();
            Player player = (Player) arguments.getSource().getEntity();
            if (player == null)
                player = FakePlayerFactory.getMinecraft(world);
            ManaData.Set.mana(player, ManaData.Get.maxMana(player));
            return 0;
        }));
    }
}
