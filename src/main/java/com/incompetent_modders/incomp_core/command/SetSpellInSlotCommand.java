package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.api.item.AbstractSpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import com.incompetent_modders.incomp_core.command.arguments.SpellArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class SetSpellInSlotCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("setSpell").requires(s -> s.hasPermission(2)).then(Commands.argument("spellSlot", IntegerArgumentType.integer(0, 5)).then(Commands.argument("spell", new SpellArgument()).executes(arguments -> {
                    ServerLevel world = arguments.getSource().getLevel();
                    Player player = (Player) arguments.getSource().getEntity();
                    if (player == null)
                        player = FakePlayerFactory.getMinecraft(world);
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof AbstractSpellCastingItem) {
                        ItemStack staff = player.getItemInHand(InteractionHand.MAIN_HAND);
                        CompoundTag tag = staff.getOrCreateTag();
                        //tag.putString("spellSlot_" + IntegerArgumentType.getInteger(arguments, "spellSlot"), SpellArgument.getSpell(arguments, "spell").getSpellIdentifier().toString());
                        SpellUtils.serializeToSlot(tag, IntegerArgumentType.getInteger(arguments, "spellSlot"), SpellArgument.getSpell(arguments, "spell"));
                    }
                    return 0;
                }))
        );
    }
}
