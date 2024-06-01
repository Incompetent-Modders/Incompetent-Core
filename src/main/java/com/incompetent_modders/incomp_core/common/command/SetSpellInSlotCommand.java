package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.common.command.arguments.SpellArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class SetSpellInSlotCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("setSpell").requires(s -> s.hasPermission(2)).then(Commands.argument("spellSlot", IntegerArgumentType.integer()).then(Commands.argument("spells", new SpellArgument()).executes(arguments -> {
                    ServerLevel world = arguments.getSource().getLevel();
                    Player player = (Player) arguments.getSource().getEntity();
                    if (player == null)
                        player = FakePlayerFactory.getMinecraft(world);
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem) {
                        ItemStack staff = player.getItemInHand(InteractionHand.MAIN_HAND);
                        CastingItemUtil.serializeToSlot(staff, IntegerArgumentType.getInteger(arguments, "spellSlot"), SpellArgument.getSpell(arguments, "spells"));
                        Component outputComponent = Component.translatable("commands.set_spell", SpellListener.getDisplayName(SpellArgument.getSpell(arguments, "spells")), IntegerArgumentType.getInteger(arguments, "spellSlot"));
                        player.displayClientMessage(outputComponent, false);
                    }
                    return 0;
                })
        ));
    }
}
