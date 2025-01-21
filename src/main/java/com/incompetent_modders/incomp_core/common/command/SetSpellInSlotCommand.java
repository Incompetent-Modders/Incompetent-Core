package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class SetSpellInSlotCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("setSpell").requires(s -> s.hasPermission(2)).then(Commands.argument("spellSlot", IntegerArgumentType.integer()).then(Commands.argument("spells", ResourceArgument.resource(context, ModRegistries.Keys.SPELL)).executes(arguments -> {
                    ServerLevel world = arguments.getSource().getLevel();
                    Player player = (Player) arguments.getSource().getEntity();
                    if (player == null)
                        player = FakePlayerFactory.getMinecraft(world);
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem) {
                        ItemStack staff = player.getItemInHand(InteractionHand.MAIN_HAND);
                        Holder.Reference<Spell> spell = ResourceArgument.getResource(arguments, "spells", ModRegistries.Keys.SPELL);
                        CastingItemUtil.serializeToSlot(staff, IntegerArgumentType.getInteger(arguments, "spellSlot"), spell.getKey());
                        Component outputComponent = Component.translatable("commands.set_spell", Spell.getDisplayName(spell.getKey()), IntegerArgumentType.getInteger(arguments, "spellSlot"));
                        player.displayClientMessage(outputComponent, false);
                    }
                    return 0;
                })
        ));
    }
}
